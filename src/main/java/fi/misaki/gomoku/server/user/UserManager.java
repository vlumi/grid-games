/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.user;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.protocol.Message;
import fi.misaki.gomoku.protocol.PushMessage;
import fi.misaki.gomoku.protocol.key.MessageType;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.websocket.Session;

/**
 *
 * @author vlumi
 */
@ApplicationScoped
public class UserManager implements Serializable {

    private static final long serialVersionUID = 6647461122604969208L;

    private static final Logger LOGGER = Logger.getLogger(UserManager.class.getName());

    /**
     *
     */
    private final Map<String, User> usersBySessionId = Collections.synchronizedMap(new HashMap<>());
    /**
     *
     */
    private final Map<String, User> usersByName = Collections.synchronizedMap(new HashMap<>());

    private final AtomicLong anonymousCounter = new AtomicLong(1);

    /**
     *
     * @param sessionId
     * @return
     */
    public User getUserForSessionId(String sessionId) {
        User user = this.usersBySessionId.get(sessionId);
        if (user == null) {
            // TODO: error
            return null;
        }
        return user;
    }

    /**
     * Attempt to start as session for the given user.
     *
     * If the name is not given, creates an anonymous session.
     *
     * If the name is given, and the user already exists, the password must not
     * be empty and must match the password used to create the user's previous
     * sessions.
     *
     * @param name Name of the user, or an empty string for anonymous.
     * @param passwordHash Hashed password for the user to allow multiple
     * connections for the user.
     * @param session The websocket session that requested the session to be
     * started.
     * @return The user connected to the session; may be a new or an existing
     * user.
     * @throws InvalidRequestException In case of any errors.
     */
    public User startSession(String name, String passwordHash, Session session)
            throws InvalidRequestException {

        LOGGER.log(Level.FINEST, "Start session: {0}", name);
        // TODO: validate username format; [^#]
        User user;
        if (name.isEmpty()) {
            user = createAnonymouseUser();
        } else {
            synchronized (this.usersByName) {
                user = this.usersByName.get(name);
                if (user == null) {
                    LOGGER.log(Level.FINEST, "- New user");
                    user = new User();
                    user.setName(name);
                    user.setPasswordHash(passwordHash);
                    this.usersByName.put(name, user);
                } else if (passwordHash.isEmpty() || !user.getPasswordHash().equals(passwordHash)) {
                    throw new InvalidRequestException("Username and password don't match.");
                }
            }
        }

        this.usersBySessionId.put(session.getId(), user);
        user.addSession(session);

        return user;
    }

    /**
     * Create an anonymous user, by a unique name.
     *
     * @return
     */
    private User createAnonymouseUser() {
        User user = new User();
        user.setName("anon#" + this.anonymousCounter.getAndAdd(1));
        LOGGER.log(Level.FINEST, "Anonymouse user: {0}", user.getName());
        synchronized (this.usersByName) {
            this.usersByName.put(user.getName(), user);
        }
        return user;
    }

    /**
     * Ends the session, removing all references to it.
     *
     * If it was the last session of the user, do special handling to mark the
     * user offline.
     *
     * @param session
     */
    public void endSession(Session session) {
        User user = this.usersBySessionId.remove(session.getId());
        if (user != null) {
            user.removeSession(session);
            if (user.getSessions().isEmpty() || user.getPasswordHash().isEmpty()) {
                // TODO: add hooks for other handlers
                synchronized (this.usersByName) {
                    this.usersByName.remove(user.getName());
                }
            }
        }
    }

    /**
     * Get a set of all members' names.
     *
     * @return
     */
    public Set<String> getMembers() {
        synchronized (this.usersByName) {
            return this.usersByName.keySet();
        }
    }

    /**
     * Get a list of all members' names, as a JsonArrayBuilder object.
     *
     * @return
     */
    public JsonArrayBuilder getMembersAsJsonArrayBuilder() {
        JsonArrayBuilder membersBuilder = Json.createBuilderFactory(null)
                .createArrayBuilder();
        this.getMembers().forEach(member -> membersBuilder.add(member));

        return membersBuilder;
    }

    /**
     * Get all currently active sessions across all users.
     *
     * @return A set of all active sessions.
     */
    public Set<Session> getAllSessions() {
        Set<Session> allSessions = new HashSet<>();
        synchronized (this.usersByName) {
            System.out.println("users by name: " + this.usersByName);
            this.usersByName.values().stream()
                    .forEach(user -> allSessions.addAll(user.getSessions()));
        }
        return allSessions;
    }

    /**
     * Send the post-login message to the user.
     *
     * @param user Target user.
     */
    public void sendPostLoginMessage(User user) {
        PushMessage loginMessage = new PushMessage(MessageType.USER);
        loginMessage.getPayload()
                .add("type", UserMessagePayloadType.LOGIN.getCode())
                .add("name", user.getName());
        this.sendMessageToUser(user, loginMessage);
    }

    /**
     * Sends a message to a user.
     *
     * @param user Target user.
     * @param message Message to send.
     */
    public void sendMessageToUser(User user, Message message) {
        String messageString = message.toJsonObject().toString();
        LOGGER.log(Level.FINEST, "Send message to user {0}: {1}", new String[]{user.getName(), messageString});

        user.getSessions().forEach(session -> {
            LOGGER.log(Level.FINEST, " - Send to session: {0}", session.getId());
            session.getAsyncRemote().sendText(messageString);
        });

    }

}

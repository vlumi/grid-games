/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.auth;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.server.GomokuServer;
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
import javax.websocket.Session;

/**
 *
 * @author vlumi
 */
@ApplicationScoped
public class UserManager implements Serializable {

    private static final long serialVersionUID = 6647461122604969208L;

    private static final Logger LOGGER = Logger.getLogger(GomokuServer.class.getName());

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
    public User getUserForSession(String sessionId) {
        User user = this.usersBySessionId.get(sessionId);
        if (user == null) {
            user = new User();
            this.usersBySessionId.put(sessionId, user);
        }
        return user;
    }

    /**
     *
     * @param name
     * @param passwordHash
     * @param session
     * @return
     * @throws InvalidRequestException
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
     *
     * @return
     */
    private User createAnonymouseUser() {
        User user = new User();
        user.setName("anon#" + this.anonymousCounter.getAndAdd(1));
        LOGGER.log(Level.FINEST, "Anonymouse user: {0}", user.getName());
        this.usersByName.put(user.getName(), user);
        return user;
    }

    /**
     *
     * @param session
     */
    public void endSession(Session session) {
        User user = this.usersBySessionId.remove(session.getId());
        if (user != null) {
            user.removeSession(session);
            if (user.getSessions().isEmpty() && user.getPasswordHash().isEmpty()) {
                this.usersByName.remove(user.getName());
            }
        }
    }

    /**
     *
     * @param name
     * @return
     */
    public boolean isNameTaken(String name) {
        return this.usersByName.containsKey(name);
    }

    public Set<String> getMembers() {
        return this.usersByName.keySet();
    }

    /**
     *
     * @return
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

}

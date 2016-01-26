package fi.misaki.grid.server.player;

import fi.misaki.grid.protocol.InvalidRequestException;
import fi.misaki.grid.protocol.Message;
import fi.misaki.grid.protocol.PushMessage;
import fi.misaki.grid.protocol.key.MessageContext;
import fi.misaki.grid.server.game.Game;
import fi.misaki.grid.server.game.GameManager;
import fi.misaki.grid.server.lobby.LobbyManager;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.websocket.Session;

/**
 * Logic for managing the players.
 *
 * @author vlumi
 */
@ApplicationScoped
public class PlayerManager implements Serializable {

    private static final long serialVersionUID = 6647461122604969208L;

    private static final Logger LOGGER = Logger.getLogger(PlayerManager.class.getName());

    @Inject
    private LobbyManager lobbyManager;
    @Inject
    private GameManager gameManager;

    /**
     * Player objects mapped by WebSocket session IDs.
     */
    private final Map<String, Player> playersBySessionId = Collections.synchronizedMap(new HashMap<>());
    /**
     * Player objects mapped by their names.
     */
    private final Map<String, Player> playersByName = Collections.synchronizedMap(new HashMap<>());
    /**
     * A counter for creating unique anonymous player names.
     */
    private final AtomicLong anonymousCounter = new AtomicLong(1);

    /**
     * Handle the login request received from a client.
     *
     * @param session The WebSocket session that sent the request.
     * @param data The data body of the received message.
     * @throws InvalidRequestException
     */
    public void handleLoginRequest(Session session, JsonObject data)
            throws InvalidRequestException {
        Player player = loginPlayer(session, data);
        LOGGER.log(Level.FINE, "Player joined: {0}", player.getName());

        sendPostLoginMessage(player);
        lobbyManager.sendInitMessage(player);

        if (player.getSessions().size() == 1) {
            // New player
            lobbyManager.sendJoinMessage(player);
        } else {
            // TODO: check if in a game room -- send status if needed
            Game game = this.gameManager.findGame(player);
            if (game != null) {
                gameManager.sendStateToSession(game, session);
            }
        }
    }

    /**
     * Attempts to login the player with the received credentials.
     *
     * @param session The WebSocket session that sent the login request.
     * @param data The data body of the received message.
     * @throws InvalidRequestException in case the login failed.
     */
    private Player loginPlayer(Session session, JsonObject data)
            throws InvalidRequestException {

        String name = data.getString("name", "");
        String password = data.getString("password", "");
        return startSession(name, password, session);
    }

    /**
     * Gets the player matching the given WebSocket session ID.
     *
     * @param sessionId The WebSocket session ID to search with.
     * @return the matching player
     * @throws InvalidRequestException in case player was not found.
     */
    public Player getPlayerForSessionId(String sessionId) throws InvalidRequestException {
        Player player = this.playersBySessionId.get(sessionId);
        if (player == null) {
            throw new InvalidRequestException("Player for session was not found.");
        }
        return player;
    }

    /**
     * Gets the player with the given name.
     *
     * @param name The name to search with.
     * @return the matching player.
     * @throws InvalidRequestException in case player was not found.
     */
    public Player getPlayerForName(String name) throws InvalidRequestException {
        synchronized (this.playersByName) {
            Player player = this.playersByName.get(name);
            if (player == null) {
                throw new InvalidRequestException("Player for name was not found.");
            }
            return player;
        }
    }

    /**
     * Attempt to start as session for the given player.
     *
     * If the name is not given, creates an anonymous session.
     *
     * If the name is given, and the player already exists, the password must
     * not be empty and must match the password used to create the player's
     * previous sessions.
     *
     * @param name Name of the player, or an empty string for anonymous.
     * @param password Hashed password for the player to allow multiple
     * connections for the player.
     * @param session The WebSocket session that requested the session to be
     * started.
     * @return The player connected to the session; may be a new or an existing
     * player.
     * @throws InvalidRequestException In case of any errors.
     */
    public Player startSession(String name, String password, Session session)
            throws InvalidRequestException {

        LOGGER.log(Level.FINEST, "Start session: {0}", name);
        // TODO: validate name format; [^#]
        Player player;
        if (name.isEmpty()) {
            player = createAnonymousPlayer();
        } else {
            if (name.matches(".*[^A-Za-z0-9].*")) {
                throw new InvalidRequestException("Name may only contain alphabets and numbers.");
            }
            synchronized (this.playersByName) {
                player = this.playersByName.get(name);
                String passwordHash = createPasswordHash(password);
                if (player == null) {
                    LOGGER.log(Level.FINEST, "- New player");
                    player = new Player();
                    player.setName(name);
                    player.setPasswordHash(passwordHash);
                    this.playersByName.put(name, player);
                } else if (password.isEmpty() || !player.getPasswordHash().equals(passwordHash)) {
                    throw new InvalidRequestException("Player name and password don't match.");
                }
            }
        }

        this.playersBySessionId.put(session.getId(), player);
        player.addSession(session);

        return player;
    }

    /**
     * Marks the players busy, propagating the status update to all players.
     *
     * @param players The players to mark busy.
     * @throws InvalidRequestException in case a player is already busy.
     */
    public void setBusy(Player... players)
            throws InvalidRequestException {
        synchronized (this) {
            for (Player player : players) {
                if (player.isBusy()) {
                    throw new InvalidRequestException("Player is busy.");
                }
            }
            for (Player player : players) {
                if (player == null) {
                    continue;
                }
                player.setStatus(PlayerStatus.BUSY);
                this.lobbyManager.sendPlayerBusy(player);
            }
        }
    }

    /**
     * Marks the players free, propagating the status update to all players.
     *
     * @param players The players to mark free.
     */
    public void setFree(Player... players) {
        synchronized (this) {
            for (Player player : players) {
                if (player == null) {
                    continue;
                }
                player.setStatus(PlayerStatus.FREE);
                this.lobbyManager.sendPlayerFree(player);
            }
        }
    }

    /**
     * Create an anonymous player, by a unique name.
     *
     * @return
     */
    private Player createAnonymousPlayer() {
        Player player = new Player();
        player.setName("anon#" + this.anonymousCounter.getAndAdd(1));
        LOGGER.log(Level.FINEST, "Anonymous player: {0}", player.getName());
        synchronized (this.playersByName) {
            this.playersByName.put(player.getName(), player);
        }
        return player;
    }

    /**
     * Ends the session, removing all references to it.
     *
     * If it was the last session of the player, do special handling to mark the
     * player offline.
     *
     * @param session
     */
    public void endSession(Session session) {
        Player player = this.playersBySessionId.remove(session.getId());
        if (player != null) {
            player.removeSession(session);
            if (player.getSessions().isEmpty() || player.getPasswordHash().isEmpty()) {
                // TODO: add hooks for other handlers
                synchronized (this.playersByName) {
                    this.playersByName.remove(player.getName());
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
        synchronized (this.playersByName) {
            return this.playersByName.keySet();
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
     * Get a set of all currently free members' names.
     *
     * @return
     */
    public Set<String> getBusyMembers() {
        synchronized (this.playersByName) {
            return this.playersByName.values().stream()
                    .filter(player -> player.isBusy())
                    .map(player -> player.getName())
                    .collect(Collectors.toSet());
        }
    }

    /**
     * Get a list of all currently busy members' names, as a JsonArrayBuilder
     * object.
     *
     * @return
     */
    public JsonArrayBuilder getBusyMembersAsJsonArrayBuilder() {
        JsonArrayBuilder membersBuilder = Json.createBuilderFactory(null)
                .createArrayBuilder();
        this.getBusyMembers().forEach(member -> membersBuilder.add(member));

        return membersBuilder;
    }

    /**
     * Get all currently active sessions across all players.
     *
     * @return A set of all active sessions.
     */
    public Set<Session> getAllSessions() {
        Set<Session> allSessions = new HashSet<>();
        synchronized (this.playersByName) {
            this.playersByName.values().stream()
                    .forEach(player -> allSessions.addAll(player.getSessions()));
        }
        return allSessions;
    }

    /**
     * Send the post-login message to the player.
     *
     * @param player Target player.
     */
    public void sendPostLoginMessage(Player player) {
        PushMessage loginMessage = new PushMessage(MessageContext.PLAYER);
        loginMessage.getData()
                .add("type", PlayerPushMessageDataType.LOGIN.getCode())
                .add("name", player.getName());
        this.sendMessage(player, loginMessage);
    }

    /**
     * Sends a message to a player.
     *
     * @param player Target player.
     * @param message Message to send.
     */
    public void sendMessage(Player player, Message message) {
        String messageString = message.toJsonObject().toString();

        sendMessage(player, messageString);

    }

    /**
     * Sends a message to players.
     *
     * @param players Target players.
     * @param message Message to send.
     */
    public void sendMessage(Set<Player> players, Message message) {
        String messageString = message.toJsonObject().toString();

        players.forEach(player -> this.sendMessage(player, messageString));
    }

    /**
     * Sends a message to a player.
     *
     * @param session Target session.
     * @param message Message to send.
     */
    public void sendMessage(Session session, Message message) {
        String messageString = message.toJsonObject().toString();

        sendMessage(session, messageString);

    }

    /**
     *
     * @param player
     * @param messageString
     */
    private void sendMessage(Player player, String messageString) {
        if (player == null) {
            return;
        }
        LOGGER.log(Level.FINEST, "Send message to player {0}: {1}", new String[]{player.getName(), messageString});
        player.getSessions().forEach(session -> {
            LOGGER.log(Level.FINEST, " - Send to session: {0}", session.getId());
            session.getAsyncRemote().sendText(messageString);
        });
    }

    /**
     * Send a message to the WebSocket session.
     *
     * @param session
     * @param messageString
     */
    private void sendMessage(Session session, String messageString) {
        Player player = this.playersBySessionId.get(session.getId());
        LOGGER.log(Level.FINEST, "Send message to player {0}: {1}", new String[]{player.getName(), messageString});
        LOGGER.log(Level.FINEST, " - Send to session: {0}", session.getId());
        session.getAsyncRemote().sendText(messageString);
    }

    /**
     * Creates a password hash from the password.
     *
     * @param password
     * @return
     */
    private static String createPasswordHash(String password) {
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            // TODO: salt with a secret stored on the server
            return bytesToHex(crypt.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, "Error creating password digest: {0}", e);
            return "";
        }
    }

    /**
     * Converts the byte array into a hexadecimal string representation.
     *
     * @param hash
     * @return
     */
    private static String bytesToHex(final byte[] hash) {
        try (Formatter formatter = new Formatter()) {
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }

}

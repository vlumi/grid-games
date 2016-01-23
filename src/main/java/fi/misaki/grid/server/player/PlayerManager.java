package fi.misaki.grid.server.player;

import fi.misaki.grid.protocol.InvalidRequestException;
import fi.misaki.grid.protocol.Message;
import fi.misaki.grid.protocol.PushMessage;
import fi.misaki.grid.protocol.key.MessageContext;
import fi.misaki.grid.server.game.Game;
import fi.misaki.grid.server.game.GameManager;
import fi.misaki.grid.server.lobby.LobbyManager;
import java.io.Serializable;
import java.util.Collections;
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
     *
     */
    private final Map<String, Player> playersBySessionId = Collections.synchronizedMap(new HashMap<>());
    /**
     *
     */
    private final Map<String, Player> playersByName = Collections.synchronizedMap(new HashMap<>());
    /**
     *
     */
    private final AtomicLong anonymousCounter = new AtomicLong(1);

    /**
     *
     * @param session
     * @param data
     * @throws InvalidRequestException
     */
    public void handleLoginRequest(Session session, JsonObject data) throws InvalidRequestException {
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
     *
     * @param request
     * @param session
     * @throws InvalidRequestException
     */
    private Player loginPlayer(Session session, JsonObject data)
            throws InvalidRequestException {

        String name = data.getString("name", "");
        String password = data.getString("password", "");
        return startSession(name, password, session);
    }

    /**
     *
     * @param sessionId
     * @return
     */
    public Player getPlayerForSessionId(String sessionId) {
        Player player = this.playersBySessionId.get(sessionId);
        if (player == null) {
            // TODO: error
            return null;
        }
        return player;
    }

    /**
     *
     * @param name
     * @return
     */
    public Player getPlayerForName(String name) {
        synchronized (this.playersByName) {
            Player player = this.playersByName.get(name);
            if (player == null) {
                // TODO: error
                return null;
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
     * @param session The websocket session that requested the session to be
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
            synchronized (this.playersByName) {
                player = this.playersByName.get(name);
                if (player == null) {
                    LOGGER.log(Level.FINEST, "- New player");
                    player = new Player();
                    player.setName(name);
                    // TODO: hash the password
                    player.setPasswordHash(password);
                    this.playersByName.put(name, player);
                } else if (password.isEmpty() || !player.getPasswordHash().equals(password)) {
                    throw new InvalidRequestException("Player name and password don't match.");
                }
            }
        }

        this.playersBySessionId.put(session.getId(), player);
        player.addSession(session);

        return player;
    }

    /**
     *
     * @param players
     * @throws fi.misaki.grid.protocol.InvalidRequestException
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
     *
     * @param players
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
     * Get a set of all currently free members' names.
     *
     * @return
     */
    public Set<String> getFreeMembers() {
        synchronized (this.playersByName) {
            return this.playersByName.values().stream()
                    .filter(player -> player.isFree())
                    .map(player -> player.getName())
                    .collect(Collectors.toSet());
        }
    }

    /**
     * Get a list of all currently free members' names, as a JsonArrayBuilder
     * object.
     *
     * @return
     */
    public JsonArrayBuilder getFreeMembersAsJsonArrayBuilder() {
        JsonArrayBuilder membersBuilder = Json.createBuilderFactory(null)
                .createArrayBuilder();
        this.getFreeMembers().forEach(member -> membersBuilder.add(member));

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
                .add("type", PlayerMessageDataType.LOGIN.getCode())
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
        LOGGER.log(Level.FINEST, "Send message to player {0}: {1}", new String[]{player.getName(), messageString});
        player.getSessions().forEach(session -> {
            LOGGER.log(Level.FINEST, " - Send to session: {0}", session.getId());
            session.getAsyncRemote().sendText(messageString);
        });
    }

    /**
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

}

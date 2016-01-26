package fi.misaki.grid.server.player;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Session;

/**
 * A player entity.
 *
 * @author vlumi
 */
public class Player implements Serializable {

    private static final long serialVersionUID = -3454841459380520035L;

    private static final Logger LOGGER = Logger.getLogger(Player.class.getName());

    /**
     * The name of the player, unique across all players.
     */
    private String name = "";
    /**
     * (Optional) hashed password, to allow the same player to be connected from
     * multiple simultaneous clients.
     */
    private String passwordHash = "";
    /**
     *
     */
    private PlayerStatus status = PlayerStatus.FREE;
    /**
     * All active sessions for the player, when the player is connected from
     * multiple clients simultaneously.
     */
    private final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public boolean isBusy() {
        return this.status == PlayerStatus.BUSY;
    }

    public boolean isFree() {
        return this.status == PlayerStatus.FREE;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public Set<Session> getSessions() {
        return sessions;
    }

    public void addSession(Session session) {
        synchronized (this.sessions) {
            this.sessions.add(session);
            LOGGER.log(Level.FINEST, "Added session, now: {0}", this.sessions);
        }
    }

    public void removeSession(Session session) {
        synchronized (this.sessions) {
            if (this.sessions.contains(session)) {
                this.sessions.remove(session);
                LOGGER.log(Level.FINEST, "Removed session, now: {0}", this.sessions);
            }
        }
    }

}

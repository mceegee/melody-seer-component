/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.carbonell.melody.seer.component;

// https://stackoverflow.com/questions/1736234/what-is-meant-by-implement-a-wrapper-method 
import com.carbonell.melody.seer.component.api.ApiClient;
import com.carbonell.melody.seer.component.api.Media;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.ArrayList;
import javax.swing.SwingWorker;

/**
 * This class works as a wrapper for ApiClient, which deals with the API.
 *
 * @author marta
 */
public class MelodySeerComponent extends JPanel implements Serializable, ActionListener {

    private ApiClient apiClient;

    private String apiUrl;
    private boolean isRunning;
    private int pollingInterval = 1000;
    private String token;
    private String lastChecked = "2020-01-01T00:01Z";

    private Timer timer;
    private javax.swing.JLabel lblIcon;

    private List<OnNewMediaAddedListener> myListeners;
    private SwingWorker<Void, String> mediaCheckerWorker = null;

    /**
     * Constructor, initializes components and deals with SwingWorker
     */
    public MelodySeerComponent() {
        initComponents();
        myListeners = new ArrayList<>();

        mediaCheckerWorker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    List<Media> newMedia = getMediaSinceLastChecked();

                    // System.out.println("Checking newMedia " + newMedia.size());
                    if (newMedia == null || newMedia.isEmpty()) {
                        return null;
                    }

                    for (OnNewMediaAddedListener listener : myListeners) {
                        // System.out.println("calling listener");
                        listener.newMediaAdded(new NewMediaEventObject(this, newMedia, lastChecked));

                    }
                } catch (Exception ex) {
                    System.out.print(ex);
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {

            }

            @Override
            protected void done() {

            }

        };
    }

    /**
     * This method initializes components, deals with the timer and with
     * <code>ApiClient</code> objects.
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        lblIcon = new javax.swing.JLabel();
        lblIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/yt.png")));
        add(lblIcon, BorderLayout.CENTER); // https://stackoverflow.com/questions/5604188/how-to-make-java-swing-components-fill-available-space

        timer = new Timer(pollingInterval, this);

        apiClient = new ApiClient(apiUrl);
    }

    /**
     * This method is a wrapper for login() method from <code>ApiClient</code>
     * class. If information is correct, returns a token
     *
     * @param user
     * @param pw
     * @return token necessary to deal with other calls to API
     * @throws Exception
     */
    public String loginToApi(String user, String pw) throws Exception {
        if (apiUrl != null && !apiUrl.isEmpty()) {
            token = apiClient.login(user, pw);
            setToken(token);
        }

        return token;
    }

    /**
     * This method uses an ID to check the nickname of a certain user. Wrapper
     * for getNickName() from <code>ApiClient</code> class.
     *
     * @param id
     * @return nickname of a user given a certain ID
     * @throws Exception
     */
    public String getNickName(int id) throws Exception {
        if (apiUrl != null && !apiUrl.isEmpty()) {
            return apiClient.getNickName(id, token);
        }
        return null;
    }

    /**
     * This method uploads a file to the cloud. It's a wrapper for
     * uploadFileMultipart from <code>ApiClient</code> class.
     *
     * @param file
     * @param downloadedFromUrl
     * @return String with response
     * @throws Exception
     */
    public String uploadFileMultipart(File file, String downloadedFromUrl) throws Exception {
        if (apiUrl != null && !apiUrl.isEmpty()) {
            return apiClient.uploadFileMultipart(file, downloadedFromUrl, token);
        }
        return null;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;

        apiClient = new ApiClient(apiUrl);
    }

    public boolean isIsRunning() {
        return isRunning;
    }

    /**
     * This method deals with the timer by starting or stopping it.
     *
     * @param isRunning
     */
    public void setIsRunning(boolean isRunning) {
        if (isRunning) {
            timer.start();
        } else {
            timer.stop();
        }
        this.isRunning = isRunning;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    /**
     * Setter for pollingInterval. If there is no delay on timer, it sets it as
     * well.
     *
     * @param pollingInterval
     */
    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
        if (timer != null) {
            timer.setDelay(pollingInterval);
        }
    }

    public String getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(String lastChecked) {
        this.lastChecked = lastChecked;
    }

    public String getToken() {
        return token;
    }

    /**
     * Setter for token. Once token is set, isRunning is true.
     *
     * @param token
     */
    public void setToken(String token) {
        this.token = token;
        if (token != null) {
            setIsRunning(true);
            // System.out.println("Logged in");
        }
    }

    // UNNECESSARY for polling
    public List<Media> getAllMedia() throws Exception {
        if (apiUrl != null && !apiUrl.isEmpty()) {
            lastChecked = getNowAsIsoString();
            return apiClient.getAllMedia(token);
        }
        return null;
    }

    /** 
     * This method downloads files to the user's computer. It's a wrapper for download() from <code>ApiClient</code> class.
     * @param id
     * @param destFile
     * @throws Exception 
     */
    public void downloadMedia(int id, File destFile) throws Exception {
        if (apiUrl != null && !apiUrl.isEmpty()) {
            apiClient.download(id, destFile, token);
        }
    }

    /**
     * This method checks if there is any new media on the cloud and, if so, adds it to a list. 
     * It only checks what's new starting a certain point in time. 
     * Once it's been checked, that becomes the new point of time from which media is going to be discovered.
     * It's a wrapper method for getMediaAddedSince() from <code>ApiClient</code> class.
     * @return
     * @throws Exception 
     */
    public List<Media> getMediaSinceLastChecked() throws Exception {
        List<Media> newMedia = new ArrayList<>();
        String timeNowISO = getNowAsIsoString();
        if (apiUrl != null && !apiUrl.isEmpty()) {
            // crear una lista de objetos que contienen media y DateTime
            List<Media> discoveredMedia = apiClient.getMediaAddedSince(lastChecked, token);
            for (Media media : discoveredMedia) {
                newMedia.add(media);
            }
        }
        lastChecked = timeNowISO;
        return newMedia;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // System.out.println("actionPerformed called: " + isRunning);
        if (!isRunning) {
            return;
        }

        mediaCheckerWorker.execute();
    }

    public void addNewMediaListener(OnNewMediaAddedListener listener) {
        myListeners.add(listener);
    }

    /**
     * Method to check current date and time, transforms it to String. 
     * @return nowAsISO, current date and time in ISO as a String
     */
    // https://stackoverflow.com/questions/3914404/how-to-get-current-moment-in-iso-8601-format-with-date-hour-and-minute
    private String getNowAsIsoString() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        return nowAsISO;
    }

}

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class SoundPlayer {

    private Clip hitClip;
    private Clip scoreClip;

    public SoundPlayer() {
        try {
            hitClip = loadClip("/hit.wav");
            scoreClip = loadClip("/score.wav");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Clip loadClip(String resourcePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        URL soundURL = getClass().getResource(resourcePath);
        if (soundURL == null) {
            throw new IOException("Arquivo de som não encontrado: " + resourcePath);
        }
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        return clip;
    }

    public void playSound(String soundName) {
        Clip clipToPlay = null;
        if ("hit.wav".equals(soundName)) {
            clipToPlay = hitClip;
        } else if ("score.wav".equals(soundName)) {
            clipToPlay = scoreClip;
        }

        if (clipToPlay != null) {
            if (clipToPlay.isRunning()) {
                clipToPlay.stop();
            }
            clipToPlay.setFramePosition(0);
            clipToPlay.start();
        }
    }
}

package audio;

import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {

    private Clip musicClip;
    private boolean musicOn = true;
    private boolean shotOn = true;
    private boolean crashOn = true;
    private boolean gameOverOn = true;

    private static final String BASE = "resources/sound-effects/";

    public void setMusicOn(boolean v) {
        musicOn = v;
        if (musicOn == false) {
            stopMusic();
        }
    }

    public void setShotOn(boolean v) {
        shotOn = v;
    }

    public void setCrashOn(boolean v) {
        crashOn = v;
    }

    public void setGameOverOn(boolean v) {
        gameOverOn = v;
    }

    public boolean isMusicOn() {
        return musicOn;
    }

    public boolean isShotOn() {
        return shotOn;
    }

    public boolean isCrashOn() {
        return crashOn;
    }

    public boolean isGameOverOn() {
        return gameOverOn;
    }

    public void playMusicLoop(String fileName) {
        stopMusic();
        if (musicOn == false) {
            return;
        }
        musicClip = loadClip(fileName);
        if (musicClip != null) {
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopMusic() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
    }

    public void playShot() {
        if (shotOn == false) {
            return;
        }
        playOneShot("mixkit-short-laser-gun-shot-1670.wav");
    }

    public void playCrash() {
        if (crashOn == false) {
            return;
        }
        playOneShot("mixkit-epic-impact-afar-explosion-2782.wav");
    }

    public void playGameOver() {
        if (gameOverOn == false) {
            return;
        }
        playOneShot("mixkit-retro-arcade-game-over-470.wav");
    }

    private void playOneShot(String fileName) {
        final Clip clip = loadClip(fileName);
        if (clip == null) {
            return;
        }
        clip.addLineListener(new LineListener() {
            @Override
            public void update(LineEvent event) {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            }
        });
        clip.start();
    }

    private Clip loadClip(String fileName) {
        File file = new File(BASE + fileName);
        if (file.exists() == false) {
            System.err.println("SoundManager: missing sound file " + file.getPath());
            return null;
        }
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            audioInputStream.close();
            return clip;
        } catch (UnsupportedAudioFileException e) {
            System.err.println("SoundManager: unsupported audio format for " + file.getName() + " (convert to .wav) — " + e.getMessage());
        } catch (Exception e) {
            System.err.println("SoundManager: failed to load " + file.getName() + " — " + e.getMessage());
        }
        return null;
    }
}
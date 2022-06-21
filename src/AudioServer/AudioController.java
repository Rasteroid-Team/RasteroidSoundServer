package AudioServer;

import communications.CommunicationController;
import communications.ProtocolDataPacket;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.File;

public class AudioController {

    private CommunicationController communicationController = new CommunicationController();
    private AudioInputStream audioInputStream;
    private Clip clip;
    private String filePath;

    public AudioController(String path){
        this.filePath = path;
        try {
            clip = AudioSystem.getClip();
        }catch (LineUnavailableException e){
            e.printStackTrace();
        }
    }

    public AudioController loadAudio(){

        try{
            File audioFile = new File(filePath);
            if (audioFile.exists()){
                audioInputStream = AudioSystem.getAudioInputStream(audioFile);
                clip.open(audioInputStream);
            }else {
                System.out.println("No se ha podido cargar el audio");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return this;
    }

    public AudioController playMusic(){
        clip.start();
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        return this;
    }

    public AudioController playSounds(){
        clip.start();
        return this;
    }
    public AudioController pause(){
        clip.stop();
        return this;
    }

    public static void main(String[] args){
        AudioController audio = new AudioController("C:\\Users\\Alberturris\\Documents\\IntelliJProjects\\RasteroidSoundServer\\src\\AudioServer\\Resources\\theGrandFinale.wav");
        audio.loadAudio();
        audio.playSounds();
    }
}

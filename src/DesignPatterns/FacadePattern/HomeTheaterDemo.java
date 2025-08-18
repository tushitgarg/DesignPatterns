package DesignPatterns.FacadePattern;

/**
 * Main class to demonstrate the Facade Pattern.
 */
public class HomeTheaterDemo {
    public static void main(String[] args) {
        // Instantiate all the complex subsystem components
        Amplifier amp = new Amplifier();
        DvdPlayer dvd = new DvdPlayer();
        Projector projector = new Projector();
        TheaterLights lights = new TheaterLights();
        Screen screen = new Screen();

        // Create the Facade, giving it references to all the components
        HomeTheaterFacade homeTheater = new HomeTheaterFacade(amp, dvd, projector, lights, screen);

        // Use the simple interface to perform a complex action
        System.out.println("--- Getting ready to watch a movie... ---");
        homeTheater.watchMovie("Raiders of the Lost Ark");

        System.out.println("\n--- Movie finished. Shutting down... ---");
        homeTheater.endMovie();
    }
}

// --- The Complex Subsystem Components ---

class Amplifier {
    public void on() { System.out.println("Amplifier on"); }
    public void off() { System.out.println("Amplifier off"); }
    public void setDvd(DvdPlayer dvd) { System.out.println("Amplifier setting DVD player"); }
    public void setSurroundSound() { System.out.println("Amplifier surround sound on (5 speakers, 1 subwoofer)"); }
    public void setVolume(int level) { System.out.println("Amplifier setting volume to " + level); }
}

class DvdPlayer {
    public void on() { System.out.println("DVD Player on"); }
    public void off() { System.out.println("DVD Player off"); }
    public void play(String movie) { System.out.println("DVD Player playing \"" + movie + "\""); }
    public void stop() { System.out.println("DVD Player stopped"); }
    public void eject() { System.out.println("DVD Player eject"); }
}

class Projector {
    public void on() { System.out.println("Projector on"); }
    public void off() { System.out.println("Projector off"); }
    public void wideScreenMode() { System.out.println("Projector in widescreen mode (16x9 aspect ratio)"); }
}

class TheaterLights {
    public void on() { System.out.println("Theater lights on"); }
    public void dim(int level) { System.out.println("Theater ceiling lights dimming to " + level + "%"); }
}

class Screen {
    public void up() { System.out.println("Theater screen going up"); }
    public void down() { System.out.println("Theater screen going down"); }
}


// --- The Facade Class ---

class HomeTheaterFacade {
    // The facade holds references to all the subsystem components
    private Amplifier amp;
    private DvdPlayer dvd;
    private Projector projector;
    private TheaterLights lights;
    private Screen screen;

    public HomeTheaterFacade(Amplifier amp, DvdPlayer dvd, Projector projector, TheaterLights lights, Screen screen) {
        this.amp = amp;
        this.dvd = dvd;
        this.projector = projector;
        this.lights = lights;
        this.screen = screen;
    }

    /**
     * The simple method that hides all the complexity.
     */
    public void watchMovie(String movie) {
        System.out.println("Get ready to watch a movie...");
        lights.dim(10);
        screen.down();
        projector.on();
        projector.wideScreenMode();
        amp.on();
        amp.setDvd(dvd);
        amp.setSurroundSound();
        amp.setVolume(5);
        dvd.on();
        dvd.play(movie);
    }

    /**
     * Another simple method to end the movie.
     */
    public void endMovie() {
        System.out.println("Shutting movie theater down...");
        lights.on();
        screen.up();
        projector.off();
        amp.off();
        dvd.stop();
        dvd.eject();
        dvd.off();
    }
}

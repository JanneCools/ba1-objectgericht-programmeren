package polis;

public class Sleeper extends StaticActor {

    public Sleeper(int r, int k, CityMap model, Simulator simulator, BuildingTile home) {
        super(r, k, r, k, model, simulator, home, "sleeper");
    }

    @Override
    public String getActorType() {
        return "sleeper";
    }

    @Override
    public void changeRole() {
        JobSeeker jobSeeker = new JobSeeker(r, k, model, simulator, home);
        home.changeResident(this, jobSeeker);
        simulator.removeActor(this);
        simulator.addActor(jobSeeker);
    }
}
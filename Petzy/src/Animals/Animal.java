package Animals;

public class Animal {
  private int energy;
  private int food;
  private int water;
  private int health;
  private int mood;

  public Animal(int energy, int food, int water, int health, int mood) {
    this.energy = energy;
    this.food = food;
    this.water = water;
    this.health = health;
    this.mood = mood;
  }

  public int getEnergy() {
    return energy;
  }

  public void setEnergy(int energy) {
    this.energy = energy;
  }

  public int getFood() {
    return food;
  }

  public void setFood(int food) {
    this.food = food;
  }

  public int getWater() {
    return water;
  }

  public void setWater(int water) {
    this.water = water;
  }

  public int getHealth() {
    return health;
  }

  public void setHealth(int health) {
    this.health = health;
  }

  public int getMood() {
    return mood;
  }

  public void setMood(int mood) {
    this.mood = mood;
  }
}


import java.util.*;

public class KnapsackProblem {
    Random random = new Random();
    private List<Item> items;
    private int maxLoad;
    private List<Chromosome> chromosomes;
    private int bestToBreed;
    private Chromosome best;
    private int firstGenerationQuantity;
    private double probability = 0.1;
    private int generationDigit;

    public KnapsackProblem(int generationDigit, int firstGenerationQuantity, int bestToBreed) {
        this.generationDigit = generationDigit;
        this.firstGenerationQuantity = firstGenerationQuantity;
        this.bestToBreed = bestToBreed;
    }

    public Fill fillKnapsackGenetic(int maxLoad, List<Item> items) {
        this.items = items;
        this.maxLoad = maxLoad;
        return fillKnapsackGenetic().generateFill();

    }

    private Chromosome fillKnapsackGenetic() {
        chromosomes = generateChromosomes(firstGenerationQuantity);
        for (int i = 0; i < generationDigit; i++) {
            List<Chromosome> crossBreed = generateCrossBreed();
            chromosomes = mutate(crossBreed);
        }
        return best;
    }

    private List<Chromosome> mutate(List<Chromosome> generation) {
        for (Chromosome chromosome : generation) {
            if (random.nextDouble() < probability) {
                chromosome = chromosome.mutate();
            }
        }
        return generation;
    }

    private List<Chromosome> generateChromosomes(int number) {
        List<Chromosome> result = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            result.add(new Chromosome());
        }
        best = result.get(0);
        return result;
    }


    private class Chromosome {
        byte[] gens;
        int fitness;
        int load;

        Chromosome(byte[] gens, int load, int fitness) {
            this.gens = gens;
            this.load = load;
            this.fitness = fitness;

        }

        Chromosome() {
            load = 0;
            fitness = 0;
            gens = new byte[items.size()];
            int startPosition = random.nextInt(gens.length);
            int toLeft = startPosition;
            int toRight = startPosition++;

            while ((toLeft != 0) || (toRight != gens.length - 1)) {
                if (toLeft != 0) {
                    if (random.nextInt(gens.length) == toLeft) {
                        gens[toLeft] = 1;
                        load += items.get(toLeft).getWeight();
                        fitness += items.get(toLeft).getCost();
                    }
                    toLeft--;
                }
                if (toRight != gens.length - 1) {
                    if (random.nextInt(gens.length) == toRight) {
                        gens[toRight] = 1;
                        load += items.get(toRight).getWeight();
                        fitness += items.get(toRight).getCost();
                    }
                    toRight++;
                }
            }

            if (load > maxLoad) {
                fitness = -100;
            }
        }

        Chromosome mutate() {
            byte[] gensMutant = new byte[items.size()];
            int fitness = 0;
            int load = 0;
            for (int i = 0; i < items.size(); i++) {
                gensMutant[i] = random.nextInt(2) == 1 ? gens[i] : (byte) random.nextInt(2);
                if (gensMutant[i] == 1) {
                    load += items.get(i).getWeight();
                    fitness += items.get(i).getCost();
                }
            }
            return new Chromosome(gensMutant, load, fitness);
        }

        Fill generateFill() {
            Set<Item> takenItems = new HashSet<>();
            for (int i = 0; i < items.size(); i++)
                if (gens[i] == 1)
                    takenItems.add(items.get(i));
            return new Fill(fitness, takenItems);
        }

        Chromosome crossover(Chromosome otherParent) {
            int load = 0;
            int fitness = 0;
            byte[] crossedGenome = new byte[gens.length];
            for (int i = 0; i < crossedGenome.length; i++) {
                if (this.gens[i] == otherParent.gens[i]) {
                    crossedGenome[i] = this.gens[i];
                    if (this.gens[i] == 1) {
                        load += items.get(i).getWeight();
                        fitness += items.get(i).getCost();
                    }
                } else {
                    crossedGenome[i] = (byte) random.nextInt(2);
                    if (crossedGenome[i] == 1) {
                        load += items.get(i).getWeight();
                        fitness += items.get(i).getCost();
                    }
                }

            }
            Chromosome child = new Chromosome(crossedGenome, load, fitness);
            return child;
        }

    }

    private List<Chromosome> generateCrossBreed() {
        List<Chromosome> result = new ArrayList<>();

        chromosomes.sort((Chromosome o1, Chromosome o2) -> Integer.compare(o2.fitness, o1.fitness));
        chromosomes = chromosomes.subList(0, bestToBreed);

        if (chromosomes.get(0).fitness > best.fitness) {
            best = chromosomes.get(0);
        }


        for (int i = 0; i < firstGenerationQuantity; i++) {
            Chromosome first = chromosomes.get(random.nextInt(bestToBreed));
            Chromosome second = chromosomes.get(random.nextInt(bestToBreed));
            result.add(first.crossover(second));
        }

        result.sort((Chromosome o1, Chromosome o2) -> Integer.compare(o2.fitness, o1.fitness));

        return result;
    }
}


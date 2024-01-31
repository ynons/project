package yinonx.apitest;

import java.util.Arrays;

public class Game {
    private String name;
    private String releaseDate;
    private double  Rating;
    private String[] platforms;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getRating() {
        return Rating;
    }

    public void setRating(double rating) {
        Rating = rating;
    }

    public String[] getPlatforms() {
        return platforms;
    }

    public void setPlatforms(String[] newPlatforms) {
        if (newPlatforms == null || newPlatforms.length == 0) {
            // If the newPlatforms array is null or empty, do nothing
            return;
        }

        // Check if the current platforms array is not null
        if (this.platforms != null) {
            // Create a new array to store the combined platforms
            String[] combinedPlatforms = new String[this.platforms.length + newPlatforms.length];

            // Copy the existing platforms to the new array
            System.arraycopy(this.platforms, 0, combinedPlatforms, 0, this.platforms.length);

            // Copy the new platforms to the new array starting from the end of the existing
            // platforms
            System.arraycopy(newPlatforms, 0, combinedPlatforms, this.platforms.length, newPlatforms.length);

            // Set the combined platforms as the new platforms
            this.platforms = combinedPlatforms;
        } else {
            // If the current platforms array is null, set the new platforms directly
            this.platforms = newPlatforms.clone();
        }
    }

    public void setPlatforms(String newPlatform) {
        if (newPlatform == null || newPlatform.isEmpty()) {
            // If the newPlatform string is null or empty, do nothing
            return;
        }

        // Check if the current platforms array is not null
        if (this.platforms != null) {
            // Create a new array to store the combined platforms
            String[] combinedPlatforms = new String[this.platforms.length + 1];

            // Copy the existing platforms to the new array
            System.arraycopy(this.platforms, 0, combinedPlatforms, 0, this.platforms.length);

            // Set the new platform as the last element in the combined array
            combinedPlatforms[this.platforms.length] = newPlatform;

            // Set the combined platforms as the new platforms
            this.platforms = combinedPlatforms;
        } else {
            // If the current platforms array is null, set the new platform directly
            this.platforms = new String[] { newPlatform };
        }
    }

    @Override
    public String toString() {
        return "Game [name=" + name + ", releaseDate=" + releaseDate + ", Rating=" + Rating + ", platforms="
                + Arrays.toString(platforms) + "]";
    }

}

// Getters and setters

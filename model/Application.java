package model;

import java.util.List;

public class Application extends Software {

    public Application(String id) {
        this.softwareId = id;
    }

    @Override
    public boolean requires(String appId) {
        if (!this.requires.contains(appId)) {
            this.requires.add(appId);
        }
		return true;
    }
    public boolean requires(List<String> softwares) {
        for (String appId: softwares) {
            this.requires(appId);
        }
        return true;
    }
    public void printInfo(String prefix){
       System.out.println(prefix+ this.softwareId + ":");
       System.out.println(prefix+ "\t Dependencies: "+ String.join(", ", this.requires));
       System.out.println(prefix+ "\t Required By: "+ String.join(", ", this.requiredBy));
    }
    public boolean isOnlyRequiredBy(String softId){
        if (requiredBy.size() ==1 && requiredBy.contains(softId)) {
            return true;
        } else {
            return false;
        }
    }
}
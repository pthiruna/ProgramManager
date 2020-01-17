package model;

import java.util.HashSet;
import java.util.Set;
// Abstract Base class - needs to be implemented by different kind of softwares - ex Applications, Plugings, Libraries etc
public abstract class Software {
    public String softwareId;
    public String softwareVersion;
    public Set<String> requires = new HashSet<String>();
    public Set<String> requiredBy = new HashSet<String>();
    abstract public boolean isOnlyRequiredBy(String softId);
    abstract public boolean requires(String software);
	public void printInfo(String string) {
    }


}
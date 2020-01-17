package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import model.Application;
import exceptions.InstallationException;
import exceptions.UninstallException;
import model.Software;

public class ProgramManager {
    private HashMap<String, Software> softwaresInstalled;
    private HashMap<String, List<String>> softwaresToInstall = new HashMap<String, List<String>>();

    public ProgramManager() {
        this.softwaresInstalled = new HashMap<String, Software>();
    }

    public void listAllInstalledSoftwares() {
        System.out.println("---------Listing all Installed Software----------");
        for (Map.Entry<String, Software> entry : this.softwaresInstalled.entrySet()) {
            // System.out.println(entry.getKey()+":");
            entry.getValue().printInfo("  ");
        }

    }

    public void sayHello() {
        System.out.println("Hello from Program manager");
    }

    private HashMap<String, List<String>> parseFile(String filePath) throws FileNotFoundException {
        HashMap<String, List<String>> softwaresMap = new HashMap<String, List<String>>();
        Scanner scanner;
        try {
            scanner = new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        // Create a map with sofwareId and its dependency. Based on the input file
        // provided.
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] keyValue = line.split("=");
            String appName = keyValue[0];
            List<String> dependencyList = (keyValue.length < 2 || keyValue[1] == "") ? new ArrayList<String>()
                    : Arrays.asList(keyValue[1].split(","));
            softwaresMap.put(appName, dependencyList);
        }
        scanner.close();
        return softwaresMap;
    }

    public boolean installSoftwaresFromFile(String filePath) throws FileNotFoundException {
        boolean installationSuccessful = true;
        this.softwaresToInstall.putAll(this.parseFile(filePath));
        for (Map.Entry<String, List<String>> entry : this.softwaresToInstall.entrySet()) {
            String softId = entry.getKey();
            List<String> depSoftwares = entry.getValue();
            try {
                this.installSoftware(softId, depSoftwares);
            } catch (InstallationException e) {
                installationSuccessful = false;
                e.printStack();
            }
        }
        this.listAllInstalledSoftwares();
        return installationSuccessful;
    }

    // TODO Detect cyclic depenencies
    private boolean installSoftware(String softId, List<String> depSoftwares) throws InstallationException {
        InstallationException iexep = new InstallationException(null);
        boolean allDependenciesInstalled = true;
        if (this.softwaresInstalled.get(softId) != null) {
            // Software is already Installed
            return true;
        } else if (this.softwaresToInstall.get(softId) != null) {
            // Software is not installed but there exists a plan in the imported file to
            // install the software
            for (String depSoftId : depSoftwares) {
                // Check if dep already Installed
                if (this.softwaresInstalled.get(depSoftId) != null) {
                    // Continue to install the next dependent software.
                    continue;
                } else {
                    // Check if depSoft is defined in the input file.
                    if (this.softwaresToInstall.get(depSoftId) != null) {
                        // Recursively call to install dependency
                        try {
                            this.installSoftware(depSoftId, this.softwaresToInstall.get(depSoftId));
                        } catch (InstallationException e) {
                            iexep.messages.addAll(e.messages);
                            iexep.messages.add("Unable to install Software: " + softId
                                    + ". One or more dependencies could not be installed.");
                            allDependenciesInstalled = false;
                        }
                    } else {
                        // Unkown software throw error
                        allDependenciesInstalled = false;
                        iexep.messages.add("Unknow Software: Unable to find a plan to install software ''" + depSoftId
                                + "'. Required for installing:" + softId);
                    }
                }
            }
        } else {
            // There is no plan defined to install this software : Error
            allDependenciesInstalled = false;
            iexep.messages.add("Unknow Software: Unable to find software ''" + softId + "'");
        }
        if (!allDependenciesInstalled) {
            throw iexep;
        }
        // Install New Software
        System.out.println("Installing software:  " + softId + "...");
        Application app = new Application(softId);
        app.requires.addAll(depSoftwares);
        this.softwaresInstalled.put(softId, app);
        // Update required by for dep software.
        for (String depSoft : depSoftwares) {
            this.softwaresInstalled.get(depSoft).requiredBy.add(softId);
        }
        System.out.println("Installed software:  " + softId);
        return true;
    }

    public boolean uninstallSoftwaresFromFile(String filePath) throws FileNotFoundException {
        HashMap<String, List<String>> softwaresToRemove = this.parseFile(filePath);
        boolean allOperationPassed = true;
        for (Map.Entry<String, List<String>> entry : softwaresToRemove.entrySet()) {
            String softId = entry.getKey();
            try {
                this.uninstallSoftware(softId, null);
            } catch (UninstallException e) {
                allOperationPassed = false;
                e.printStack();
            }
        }
        this.listAllInstalledSoftwares();
        return allOperationPassed;
    }

    private boolean uninstallSoftware(String softId, String parentSoftware) throws UninstallException {
        UninstallException iexep = new UninstallException(null);
        Software software = this.softwaresInstalled.get(softId);
        boolean removeSoftware = false;
        if (software == null) {
            // Software has already been removed.
            return true;
        }
        // Check if any software depends on this software
        if (software.requiredBy.size() == 0 || software.isOnlyRequiredBy(parentSoftware)) {
            // No Software depends on this software. Can be safely removed.
            removeSoftware = true;
            // Check if associated software can be removed.
            Set<String> depSoftwares = this.softwaresInstalled.get(softId).requires;
            for (String depSoftId : depSoftwares) {
                uninstallSoftware(depSoftId, softId);
            }
        } else {
            if (parentSoftware != null) {
                // no-op sotware has other dependencies.
                software.requiredBy.remove(parentSoftware);
                return true;
            }
        }

        // No error message needs to be displayed when you are unable to remove child
        // softwares.
        if (!removeSoftware && parentSoftware == null) {
            iexep.messages.add("Unable to Uninstall Software: " + softId + " .The following Software have dependency: "
                    + software.requiredBy.toString());
            throw iexep;
        }
        this.softwaresInstalled.remove(softId);
        return true;
    }

}
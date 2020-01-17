import core.ProgramManager;

class Main {
    public static void main(String[] args) {
        String appsInstallFilePath = "./testFiles/appsToInstall.txt";
        String uninstallFilePath = "./testFiles/uninstall.txt";

        System.out.println("Installing applications from: " + appsInstallFilePath);
        ProgramManager pm = new ProgramManager();
        try {
        if (pm.installSoftwaresFromFile(appsInstallFilePath)) {
            System.out.println("All Applications successfully Installed");
        }
        }catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("Uninstalling applications from: " + uninstallFilePath);
        try {
            if (pm.uninstallSoftwaresFromFile(uninstallFilePath)) {
                System.out.println("All Applications successfully Uninstalled");
            }else {
                System.out.println("NOT ALL  Applications were successfully Uninstalled");

            }
            }catch(Exception e) {
                e.printStackTrace();
            }

    }
}
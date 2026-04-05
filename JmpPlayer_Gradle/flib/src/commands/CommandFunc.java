package commands;

public class CommandFunc {

    protected String command = "";
    protected int needParams = 0;
    protected String description = "";
    protected String[] paramsDescription;

    public CommandFunc() {
    }

    protected void setInfo(String command, int needParams, String[] paramsDescription, String description) {
        this.command = command;
        this.needParams = needParams;
        this.description = description;
        this.paramsDescription = paramsDescription;
    }

    public String getCommand() {
        return command;
    }

    public int getNeedParams() {
        return needParams;
    }

    public String getDesctiption() {
        return description;
    }

    public String[] getParamsDesctiption() {
        return paramsDescription;
    }

    public boolean exec(String[] params) {
        /* 継承先に記述 */
        return true;
    }

    public boolean checkCommand(String sCommand) {
        if (sCommand.equalsIgnoreCase(this.command) == true) {
            return true;
        }
        return false;
    }

    public boolean isValidParams(String[] params) {
        if (needParams == 0) {
            return true;
        }

        if (params == null) {
            return false;
        }
        if (params.length < needParams) {
            return false;
        }
        return true;
    }

    public void outputManual() {
        System.out.print("    " + command);
        if (paramsDescription != null) {
            for (int i = 0; i < paramsDescription.length; i++) {
                System.out.print(" " + paramsDescription[i]);
            }
        }
        System.out.println();
        System.out.println("        " + description);
    }
}

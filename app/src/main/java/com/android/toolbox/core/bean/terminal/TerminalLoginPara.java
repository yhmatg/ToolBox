package com.android.toolbox.core.bean.terminal;

public class TerminalLoginPara {

    private String terminalSNOrMacAddr;
    private String terminalSecret;

    public TerminalLoginPara(String terminalSNOrMacAddr, String terminalSecret) {
        this.terminalSNOrMacAddr = terminalSNOrMacAddr;
        this.terminalSecret = terminalSecret;
    }

    public String getTerminalSNOrMacAddr() {
        return terminalSNOrMacAddr;
    }

    public void setTerminalSNOrMacAddr(String terminalSNOrMacAddr) {
        this.terminalSNOrMacAddr = terminalSNOrMacAddr;
    }

    public String getTerminalSecret() {
        return terminalSecret;
    }

    public void setTerminalSecret(String terminalSecret) {
        this.terminalSecret = terminalSecret;
    }
}

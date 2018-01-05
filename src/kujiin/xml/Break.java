package kujiin.xml;

import java.time.LocalDate;

class Break {
    private LocalDate start;
    private LocalDate end;

    public Break() {}
    public Break(LocalDate starttime) {
        this.start = starttime;
    }

    public void endbreak() {
        end = LocalDate.now();
    }
}

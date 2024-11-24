module org.basdat.basdatchemicals {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires jdk.compiler;

    opens org.basdat.basdatchemicals to javafx.fxml;
    exports org.basdat.basdatchemicals;
    exports org.basdat.basdatchemicals.popups;
    opens org.basdat.basdatchemicals.popups to javafx.fxml;
    exports org.basdat.basdatchemicals.entries;
    opens org.basdat.basdatchemicals.entries to javafx.fxml;
    exports org.basdat.basdatchemicals.tablerows;
    opens org.basdat.basdatchemicals.tablerows to javafx.base;
}
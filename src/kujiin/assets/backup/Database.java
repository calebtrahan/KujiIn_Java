//package kujiin;
//
//import java.sql.*;
//import java.text.SimpleDateFormat;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//import javafx.beans.property.IntegerProperty;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.StringProperty;
//import javafx.scene.control.Alert;
//import kujiin.dialogs.*;
//
//public class Database {
//    Connection c = null;
//    Root root;
//    Statement stmt = null;
//    int sessionid;
//    public final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
//    public final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
////    public ArrayList<SessionRow> sessions = new ArrayList<>();
////    public ArrayList<PrematureEnding> prematureendings = new ArrayList<>();
//    public Goals goals;
//
//    public Database(Root root) {
//        this.root = root;
//        try {
//            this.c = DriverManager.getConnection("jdbc:sqlite:" + This_Session.sessiondatabase.getAbsolutePath());
//            stmt = c.createStatement();
//        }
//        catch ( Exception e ) {System.err.println( e.getClass().getName() + ": " + e.getMessage() );}
//        System.out.println("Opened database successfully");
//        this.root.NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
//        this.root.NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
//        this.root.ProgressColumn.setCellValueFactory(cellData -> cellData.getValue().formattedduration);
//        goals = new Goals(this, root);
//        goals.updategoaltracker();
////        deleteallgoals();
////        getsessions();
////        getcurrentgoals();
////        getcompletedgoals();
//    }
//
//    public boolean testifnosessions() {
//        try {
//            ResultSet rs = stmt.executeQuery("SELECT * FROM SESSIONS");
//            return rs.next();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public void getsessions() {
//        try {
//            String sql = "Select * From Sessions";
//            ResultSet rs = stmt.executeQuery(sql);
//            int count = 1;
//            while (rs.next()) {
//                sessions.add(new SessionRow(
//                        count, rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6),
//                        rs.getInt(7), rs.getInt(8), rs.getInt(9), rs.getInt(10), rs.getInt(11), rs.getInt(12),
//                        rs.getInt(13), rs.getInt(14)));
//                count++;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void displaylistofsession() {
//        getsessions();
//        if (sessions.size() > 0) {
//            DisplaySessionListDialog a = new DisplaySessionListDialog(null, this);
//            a.showAndWait();
//        } else {
//            Alert a = new Alert(Alert.AlertType.INFORMATION);
//            a.setTitle("No Sessions");
//            a.setHeaderText("Nothing To Display");
//            a.setContentText("No Sessions Practiced Yet");
//            a.showAndWait();
//        }
//    }
//
//    public void displayprematureendings() {
//        getprematureendings();
//        if (prematureendings.size() > 0) {
//            DisplayPrematureEndingsDialog a = new DisplayPrematureEndingsDialog(null, this);
//            a.showAndWait();
//        } else {
//            Alert a = new Alert(Alert.AlertType.INFORMATION);
//            a.setTitle("No Premature Endings");
//            a.setHeaderText("Nothing To Display");
//            a.setContentText("No Premature Endings. Great Work!");
//            a.showAndWait();
//        }
//    }
//
////     Get Premature Endings From Database And Add To prematureendings Field
////    public void getprematureendings() {
////        if (prematureendings.size() > 0) {prematureendings.clear();}
////        try {
////            ResultSet rs = stmt.executeQuery("Select * From PrematureEndings");
////            while (rs.next()) {
////                prematureendings.add(new PrematureEnding(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
////            }
////        } catch (SQLException e) {e.printStackTrace();}
////    }
////     <------------------------------------------ PLAYBACK -----------------------------------------------> //
////
////     Create Database + Tables If They Don't Exist
////    public void createtables() {
////        String sessionstablesql = "CREATE TABLE IF NOT EXISTS Sessions (" +
////                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
////                "DATEPRACTICED VARCHAR(10), " +
////                "Presession INTEGER, " +
////                "Rin INTEGER, " +
////                "Kyo INTEGER, " +
////                "Toh INTEGER, " +
////                "Sha INTEGER, " +
////                "KAI INTEGER, " +
////                "JIN INTEGER, " +
////                "Retsu INTEGER, " +
////                "Zai INTEGER, " +
////                "Zen INTEGER, " +
////                "Postsession INTEGER, " +
////                "TOTAL INTEGER)";
////        String prematureendingssql = "CREATE TABLE IF NOT EXISTS PrematureEndings (" +
////                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
////                "DATEPRACTICED TEXT, " +
////                "CUT TEXT, " +
////                "SESSIONNAME TEXT, " +
////                "REASON TEXT)";
////        String goalstablesql = "CREATE TABLE IF NOT EXISTS Goals (" +
////                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
////                "HOURS INTEGER, " +
////                "DUEDATE TEXT)";
////        String completedgoalstablesql = "CREATE TABLE IF NOT EXISTS CompletedGoals (" +
////                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
////                "HOURS INTEGER, " +
////                "DATECOMPLETED TEXT)";
////        try {
////            stmt.executeUpdate(sessionstablesql);
////            stmt.executeUpdate(prematureendingssql);
////            stmt.executeUpdate(goalstablesql);
////            stmt.executeUpdate(completedgoalstablesql);
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////    }
////
////     Create A New This_Session When Started Playback
////    public void createnewsession() {
////        try {
////            PreparedStatement sql = c.prepareStatement("INSERT INTO Sessions ( DATEPRACTICED, Presession, Rin, Kyo, Toh, Sha, KAI, JIN, Retsu, Zai, Zen, Postsession, TOTAL ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
////            sql.setString(1, Tools.gettodaysdate());
////            sql.setInt(2, 0);
////            sql.setInt(3, 0);
////            sql.setInt(4, 0);
////            sql.setInt(5, 0);
////            sql.setInt(6, 0);
////            sql.setInt(7, 0);
////            sql.setInt(8, 0);
////            sql.setInt(9, 0);
////            sql.setInt(10, 0);
////            sql.setInt(11, 0);
////            sql.setInt(12, 0);
////            sql.setInt(13, 0);
////            sql.executeUpdate();
////            ResultSet rs = stmt.executeQuery("SELECT max(ID) FROM Sessions");
////            sessionid = rs.getInt(1);
////        } catch (SQLException e) {e.printStackTrace();}
////    }
////
////     Update The Duration For A Cut The User Just Finished Practicing
////    public void updatecut(Cut currentcut, Integer cutshortduration) throws SQLException {
////        int previousvalue = 0;
////        int newvalue;
////        if (cutshortduration == null) {
////            String sql = "UPDATE Sessions set" + currentcut.name + "=" + currentcut.getdurationinminutes() + " where ID=" + sessionid + ";";
////            stmt.executeUpdate(sql);
////            String sql2 = "SELECT TOTAL FROM Sessions WHERE ID = " + sessionid;
////            ResultSet rs = stmt.executeQuery(sql2);
////            if (rs.next()) {previousvalue = rs.getInt(0);}
////            newvalue = previousvalue + currentcut.getdurationinminutes();
////            String sql3 = "UPDATE Sessions SET TOTAL=" + newvalue + " WHERE ID=" + sessionid;
////            stmt.executeUpdate(sql3);
////        } else {
////            String sql = "UPDATE Sessions set" + currentcut.name + "=" + cutshortduration + " where ID=" + sessionid + ";";
////            stmt.executeUpdate(sql);
////            String sql2 = "SELECT TOTAL FROM Sessions WHERE ID = " + sessionid;
////            ResultSet rs = stmt.executeQuery(sql2);
////            if (rs.next()) {previousvalue = rs.getInt(0);}
////            newvalue = previousvalue + cutshortduration;
////            String sql3 = "UPDATE Sessions SET TOTAL=" + newvalue + " WHERE ID=" + sessionid;
////            stmt.executeUpdate(sql3);
////        }
////    }
////
////    // Delete The This_Session From The Table If Cut Durations Are All Zero
////    public void deleteifsessionempty() throws SQLException {
////        stmt = c.createStatement();
////        ResultSet rs = stmt.executeQuery( "SELECT  Rin, Kyo, Toh, Sha, KAI, JIN, Retsu, Zai, Zen FROM Sessions WHERE ID=" + this.sessionid);
////        boolean sessionisempty = true;
////        while ( rs.next() ) {
////            if (rs.getInt("Rin") != 0) {sessionisempty = false; break;}
////            if (rs.getInt("Kyo") != 0) {sessionisempty = false; break;}
////            if (rs.getInt("Toh") != 0) {sessionisempty = false; break;}
////            if (rs.getInt("Sha") != 0) {sessionisempty = false; break;}
////            if (rs.getInt("KAI") != 0) {sessionisempty = false; break;}
////            if (rs.getInt("JIN") != 0) {sessionisempty = false; break;}
////            if (rs.getInt("Retsu") != 0) {sessionisempty = false; break;}
////            if (rs.getInt("Zai") != 0) {sessionisempty = false; break;}
////            if (rs.getInt("Zen") != 0) {sessionisempty = false; break;}
////        }
////        if (sessionisempty) {
////            // Delete This This_Session
////            stmt.executeQuery("DELETE FROM Sessions WHERE ID=" + sessionid);
////        }
////    }
////
////     <---------------------------------------- GOALS ---------------------------------------------------> //
////
////     Updates The Main UI With The Current Goal Status
////
////
////
////     <------------------------------------ PREMATURE ENDINGS -------------------------------------------> //
////
////    // Write A New Premature Ending
////    public void writeprematureending(String cutname, ArrayList<Integer> sessiontimeslist, String reason) {
////        try {
////            PreparedStatement sql = c.prepareStatement("INSERT INTO PrematureEndings ( DATEPRACTICED, CUT, SESSIONNAME, REASON ) VALUES (?, ?, ?, ?)");
////            sql.setString(1, Tools.gettodaysdate());
////            sql.setString(2, cutname);
////            sql.setString(3, sessiontimeslist.toString());
////            sql.setString(4, reason);
////            sql.executeUpdate();
////        } catch (SQLException e) {e.printStackTrace();}
////    }
////
////     <-------------------------- DATABASE AND TOTAL PROGRESS WIDGET ------------------------------------> //
////
////    // Get Total Progress An
////    public void getdetailedprogress(){
////        try {
////            ResultSet rs = stmt.executeQuery("SELECT * FROM Sessions;");
////            ArrayList<TotalProgressRow> allprogressrows = new ArrayList<>();
////            int rin = 0;
////            int kyo = 0;
////            int toh = 0;
////            int sha = 0;
////            int kai = 0;
////            int jin = 0;
////            int retsu = 0;
////            int zai = 0;
////            int zen = 0;
////            ArrayList<Integer> durations;
////            while (rs.next()) {
////                rin += rs.getInt("Rin");
////                kyo += rs.getInt("Kyo");
////                toh += rs.getInt("Toh");
////                sha += rs.getInt("Sha");
////                kai += rs.getInt("KAI");
////                jin += rs.getInt("JIN");
////                retsu += rs.getInt("Retsu");
////                zai += rs.getInt("Zai");
////                zen += rs.getInt("Zen");
////            }
////            durations = new ArrayList<>(Arrays.asList(rin, kyo, toh, sha, kai, jin, retsu, zai, zen));
////            ArrayList<String> names = new ArrayList<>(This_Session.allnames.subList(1, 10));
////            // Test Here To See If They Are All Zero
////            for (int i = 0; i < names.size(); i++) {
////                String duration;
////                if (durations.get(i) > 0) {duration = Tools.minutestoformattedhoursandmins(durations.get(i));}
////                else {duration = "No Practiced Time";
////                }
////                allprogressrows.add(new TotalProgressRow(This_Session.allnames.indexOf(names.get(i)), names.get(i), duration));
////            }
////            root.progresstable.getItems().addAll(allprogressrows);
////        } catch (SQLException e) {e.printStackTrace();}
////    }
////
////     Gett Total Hours Practiced
////    public double gettotalpracticedhours() {
////        double minutes = 0.0;
////        try {
////            ArrayList<String> cutnames = new ArrayList<>(This_Session.allnames.subList(1, 10));
////            for (String i : cutnames) {
////                ResultSet rs = stmt.executeQuery("SELECT " + i + " FROM Sessions");
////                int count = 0;
////                while (rs.next()) {
////                    minutes += rs.getDouble(count);
////                    count++;
////                }
////            }
////        } catch (SQLException ignored) {}
////        return minutes / 60;
////    }
////
////    public double getcurrentgoalhours() {
////        return 99.9;
////    }
////
////    public String getcurrenthoursformatted() {
////        return "0.0 hrs";
////    }
////
////    public String getgoalhoursformatted() {
////        return "99.9 hrs";
////    }
//
//
//}

package com.pocan.pticket.database;

import com.pocan.pticket.modules.FileModule;
import com.pocan.pticket.pTicket;


import java.io.*;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseOperation {
    private Connection con = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;

    public DatabaseOperation() {
        String request = "jdbc:mysql://" + pTicket.getInstance().getConfig().get("DatabaseHost") + ":" + pTicket.getInstance().getConfig().get("DatabasePort") + "/" + pTicket.getInstance().getConfig().get("DatabaseDatabase") + "?useUnicode=true&characterEncoding=utf8";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(request, pTicket.getInstance().getConfig().get("DatabaseUsername"), pTicket.getInstance().getConfig().get("DatabasePassword"));
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DatabaseOperation.class.getName()).log(Level.SEVERE, null, ex);

            System.exit(-1);
            /*
                Closing the main thread beacuse of the SQL server offline. (for safe)
             */
        }
    }

    public Connection getConnection() {
        String request = "jdbc:mysql://" + pTicket.getInstance().getConfig().get("DatabaseHost") + ":" + pTicket.getInstance().getConfig().get("DatabasePort") + "/" + pTicket.getInstance().getConfig().get("DatabaseDatabase") + "?useUnicode=true&characterEncoding=utf8";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(request, pTicket.getInstance().getConfig().get("DatabaseUsername"), pTicket.getInstance().getConfig().get("DatabasePassword"));
            System.out.println("Bağlantı başarılı.");
            return con;

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DatabaseOperation.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Bağlantı başarısız.");
            return null;
        }
    }

    public int getCurrentTicketID() {
        String request = "SELECT * from general";
        int ticketID = 0;
        try {
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while (rs.next()) {
                ticketID = rs.getInt("ticketID");
            }
            return ticketID;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setCurrentTicketID(int value) {
        String request = "UPDATE general SET ticketID = ?";
        try (final var statement = con.prepareStatement(request)) {
            statement.setInt(1, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean getTicketCloseReasonStatus(String memberID) {
        String request = "SELECT * from staff";
        boolean ticketID = false;
        try {
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while (rs.next()) {
                ticketID = rs.getBoolean("ticketID");
            }
            return ticketID;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isDiscordAccountExistsInStaffDatabase(String discordID) {
        String request = "SELECT * FROM staff WHERE DiscordID = '" + discordID + "'";
        try {
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(request);
            if (!rs.next()) {
                return false;
            }
            else {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isDiscordChannelIsClaimed(String ticketChannelID) {
        String request = "SELECT * FROM ticket WHERE ticketChannelID = '" + ticketChannelID + "'";
        int ticketClaimStatus = -1;
        try {
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while (rs.next()) {
                ticketClaimStatus = rs.getInt("ticketClaim");
            }
            if (ticketClaimStatus == 1) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean getCloseTicketWithoutReasonStatus(String discordID) {
        String request = "SELECT * FROM staff WHERE DiscordID = '" + discordID + "'";
        boolean outputCloseTicketWithoutReasonStatus = false;
        try {
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while (rs.next()) {
                outputCloseTicketWithoutReasonStatus = rs.getBoolean("closeTicketWithoutReason");
            }
            return outputCloseTicketWithoutReasonStatus;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setCloseTicketWithoutReasonStatus(String discordID, int value) {
        String request = "UPDATE staff SET closeTicketWithoutReason = ? WHERE DiscordID = " + discordID;
        try (final var statement = con.prepareStatement(request)) {
            statement.setInt(1, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getTicketCreaterID(String ticketChannelID) {
        String request = "SELECT * FROM ticket WHERE ticketChannelID = '" + ticketChannelID + "'";
        String ticketCreatorID = null;
        try {
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while (rs.next()) {
                ticketCreatorID = rs.getString("ticketOwnerID");
            }
            return ticketCreatorID;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Timestamp getTicketCreateDate(String ticketChannelID) {
        String request = "SELECT * FROM ticket WHERE ticketChannelID = '" + ticketChannelID + "'";
        Timestamp ticketCreateDate = null;
        try {
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while (rs.next()) {
                ticketCreateDate = rs.getTimestamp("ticketCreateDate");
            }
            return ticketCreateDate;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void setTicketClaimStatus(String ticketChannelID, int value) {
        String request = "UPDATE ticket SET ticketClaim = ? WHERE ticketChannelID = " + ticketChannelID;
        try (final var statement = con.prepareStatement(request)) {
            statement.setInt(1, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setTicketClaimStaff(String ticketChannelID, String discordMemberID) {
        String request = "UPDATE ticket SET ticketClaimedBy = ? WHERE ticketChannelID = " + ticketChannelID;
        try (final var statement = con.prepareStatement(request)) {
            statement.setString(1, discordMemberID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getStaffClaimAmount(String staffMemberID) {
        String request = "SELECT * FROM staff WHERE discordID = '" + staffMemberID + "'";
        int ticketClaim = 0;
        try {
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while (rs.next()) {
                ticketClaim = rs.getInt("ticketClaim");
            }
            return ticketClaim;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getTicketOwnerEmail(String ticketChannelID) {
        String request = "SELECT * FROM ticket WHERE ticketChannelID = '" + ticketChannelID + "'";
        String ticketOwnerEmail = null;
        try {
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while (rs.next()) {
                ticketOwnerEmail = rs.getString("ticketOwnerEmail");
            }
            return ticketOwnerEmail;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTicketOwnerNickname(String ticketID) {
        String request = "SELECT * FROM ticket WHERE ticketID = '" + ticketID + "'";
        String ticketOwnerNickname = null;
        try {
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while (rs.next()) {
                ticketOwnerNickname = rs.getString("ticketOwnerNickname");
            }
            return ticketOwnerNickname;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTicketHTML(String ticketID) {
        String request = "SELECT * FROM ticket WHERE ticketID = '" + ticketID + "'";
        String ticketTranscript = null;
        try {
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while (rs.next()) {
                ticketTranscript = rs.getString("ticketTranscript");
            }
            return ticketTranscript;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setStaffClaim(String staffMemberID, int value) {
        String request = "UPDATE staff SET ticketClaim = ? WHERE discordID = " + staffMemberID;
        try (final var statement = con.prepareStatement(request)) {
            statement.setInt(1, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addStaffToDatabase(String discordMemberID, String discordMemberName, int closeTicketWithoutReason) {
        String request = "INSERT Into staff (discordID,discordMemberName,closeTicketWithoutReason,ticketClaim) VALUES (?,?,?,?)";
        try (final var statement = con.prepareStatement(request)) {
            statement.setString(1, discordMemberID);
            statement.setString(2, discordMemberName);
            statement.setInt(3, closeTicketWithoutReason);
            statement.setInt(4, 0);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTranscriptHTMLToDatabase(String ticketChannelID, String HTMLValue) {
        String request = "UPDATE ticket SET ticketTranscript = ? WHERE ticketChannelID = " + ticketChannelID;
        try (final var statement = con.prepareStatement(request)) {
            statement.setString(1, HTMLValue);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTicketLogTXTToDatabase(String ticketChannelID, FileReader fileReader) {
        String request = "UPDATE ticket SET ticketLogText = ? WHERE ticketChannelID = " + ticketChannelID;
        try (final var statement = con.prepareStatement(request)) {
            statement.setCharacterStream(1, fileReader);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public File getTicketLogTXT(String ticketChannelID) {
        String request = "SELECT * FROM ticket WHERE ticketChannelID = '" + ticketChannelID + "'";
        File ticketTXTFile = null;
        String filePath = null;
        try {
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while (rs.next()) {
                int ticketID = rs.getInt("ticketID");
                Clob clob = rs.getClob("ticketLogText");
                Reader reader = clob.getCharacterStream();
                filePath = FileModule.getClassLocation() + "//pTicket//ticket-logs//" + ticketID + "//logg.txt";
                FileWriter writer = new FileWriter(filePath);
                int i;
                while ((i = reader.read())!=-1) {
                    writer.write(i);
                }
                writer.close();
                System.out.println(filePath);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return new File(filePath);
    }

    public void addTicketToDatabase(String ticketID, String ticketType, String ticketChannelID, String ticketOwnerNickname, String ticketOwnerID, String ticketInformation, String ticketOwnerEmail, Instant ticketCreateDate) {
        final var request = "INSERT Into ticket (ticketID,ticketType,ticketChannelID,ticketOwnerNickname,ticketOwnerID,ticketInformation,ticketOwnerEmail,ticketCreateDate,ticketTranscript) VALUES (?,?,?,?,?,?,?,?,?)";
        try (final var statement = con.prepareStatement(request)) {
            statement.setString(1, ticketID);
            statement.setString(2, ticketType);
            statement.setString(3, ticketChannelID);
            statement.setString(4, ticketOwnerNickname);
            statement.setString(5, ticketOwnerID);
            statement.setString(6, ticketInformation);
            statement.setString(7, ticketOwnerEmail);
            statement.setTimestamp(8, Timestamp.from(ticketCreateDate));
            statement.setString(9, "-");
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            //No catch here, the exception should be handled by the caller
        }
    }

    public void addTicketMemberToDatabase(String ticketID, String ticketType, String ticketChannelID, String ticketMember, String ticketMemberID) {
        String request = "INSERT Into ticket_member (ticketID,ticketType,ticketChannelID,ticketMember,ticketMemberID) VALUES (?,?,?,?,?)";
        try (final var statement = con.prepareStatement(request)) {
            statement.setString(1, ticketID);
            statement.setString(2, ticketType);
            statement.setString(3, ticketChannelID);
            statement.setString(4, ticketMember);
            statement.setString(5, ticketMemberID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteAddedTicketMembesrs(String ticketChannelID, String ticketMemberID) {
        String request = "DELETE from temp_staff_embedmessage WHERE ticketChannelID = " + ticketChannelID;
        try {
            preparedStatement = con.prepareStatement(request);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<String> getTicketAddedMembers(String ticketChannelID) {
        ArrayList<String> output = new ArrayList<String>();
        String request = "SELECT * FROM ticket_member WHERE ticketChannelID = '" + ticketChannelID + "'";
        try {
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while(rs.next()) {
                String ticketMemberID = rs.getString("ticketMemberID");
                output.add(ticketMemberID);
            }
            return output;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteAddedTicketMembers(String ticketChannelID, String ticketMemberID) {
        String request = "DELETE from ticket_member WHERE ticketChannelID = " + ticketChannelID + " and ticketMemberID = " + ticketMemberID;
        try {
            preparedStatement = con.prepareStatement(request);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setTicketClosedMember(String ticketChannelID, String value) {
        String request = "UPDATE ticket SET ticketClosedBy = ? WHERE ticketChannelID = " + ticketChannelID;
        try (final var statement = con.prepareStatement(request)) {
            statement.setString(1, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setTicketCloseReason(String ticketChannelID, String value) {
        String request = "UPDATE ticket SET ticketCloseReason = ? WHERE ticketChannelID = " + ticketChannelID;
        try (final var statement = con.prepareStatement(request)) {
            statement.setString(1, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

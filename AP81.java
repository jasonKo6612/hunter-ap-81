/**
 * History： <br>
 * 2008/8/13 Sean.Chen Created<br>
 * 2008/11/12 Sean.chen modify BEP00-00001-1237 獵才派遣共用元件整合專案<br> 
 * 2012/09/06 sally.huang modify db migration<br>
 * 2012/10/08 update by Josie Wu at BEP00-00001-1462 MySQL轉換作業專案<br>
 * 2015/10/30 update by Josie Wu at BEP00-00001-1538 機敏性欄位加密處理(AES)(1)<br>
 * 2016/09/05 update by Peter.Tsai at 調整AP架構<br>
 * 20230117 Peter Tsai HTHUNTERREQ-1237 [獵才]AP 版更
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.ht.util.XmlGlobalHandlerNewAP;
//import com.ht.util.XmlLocalHandlerNewAP;

/**
 * Description： 104人選聯絡紀錄 <br>
 * Classname：AP81.java<br>
 * Date：2008/8/13<br>
 * Author：Sean Chen<br>
 * Copyright (c) 104hunter All Rights Reserved.<br>
 */
public class AP81 {
    private static XmlGlobalHandlerNewAP globalXML = null;
    //private static XmlLocalHandlerNewAP localXML = null;
	
    // Log
    private static File fLogFile = null;
    private static BufferedWriter bwLogFile = null;
    private static PrintWriter pw = null;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd hh:mm:ss" );

    /**
     * <summary>查詢資料</summary><br>
     * @param
     * @return
     * @throws IOException
     */
    private void queryData() throws IOException {
        Connection conHun = null;
        PreparedStatement pstHun = null;
        PreparedStatement pstHun2 = null;
        PreparedStatement pstHun3 = null;
        ResultSet rsHun = null;
        ResultSet rsHun2 = null;
        try {
            // 建立Connection
            Class.forName( globalXML.getGlobalTagValue( "dsn1.driver" ) );
            conHun = DriverManager.getConnection( globalXML.getGlobalTagValue( "dsn1.database" ), globalXML.getGlobalTagValue( "dsn1.username" ), globalXML.getGlobalTagValue( "dsn1.password" ) );
            pstHun = conHun.prepareStatement( "select contact_id,rid from contact_rec where rid<>'0' and (contact_name='' or contact_name is null) order by Rid" );
            pstHun2 = conHun.prepareStatement( "Select CName,EName From Resume where Rid=?" );
            pstHun3 = conHun.prepareStatement( "Update Contact_Rec set contact_name=? where contact_id=?" );
            bwLogFile.write( "##現有連絡紀錄中 Rid<>0 之人選編號)SQL：" + "select contact_id,rid from contact_rec where rid<>0 and (contact_name='' or contact_name is null) order by Rid" + "\r\n" );
            rsHun = pstHun.executeQuery();
            while( rsHun.next() ) {
                bwLogFile.write( "##                               人選編號：" + rsHun.getString( "rid" ) + "\r\n" );
                pstHun2.clearParameters();
                pstHun2.setString( 1, rsHun.getString( "rid" ) );
                rsHun2 = pstHun2.executeQuery();
                while( rsHun2.next() ) {
                    String strName = "";
                    if( rsHun2.getString( "CName" ) != null && !rsHun2.getString( "CName" ).equals( "" ) ) {
                        strName = rsHun2.getString( "CName" );
                    } else if( rsHun2.getString( "EName" ) != null && !rsHun2.getString( "EName" ).equals( "" ) ) {
                        strName = rsHun2.getString( "EName" );
                    }
                    //bwLogFile.write( "##                                人選姓名：" + strName + "\r\n" );
                    // 更新姓名
                    if( strName.length() != 0 ) {
                        pstHun3.clearParameters();
                        pstHun3.setString( 1, strName );
                        pstHun3.setString( 2, rsHun.getString( "contact_id" ) );
                        pstHun3.executeUpdate();
                    }
                }
            }
        } catch( ClassNotFoundException e ) {
            bwLogFile.write( "##不成功##\r\n" );
            bwLogFile.write( "queryData() exception :\r\n" );
            e.printStackTrace( pw );
        } catch( SQLException e ) {
            bwLogFile.write( "##不成功##\r\n" );
            bwLogFile.write( "queryData() exception :\r\n" );
            e.printStackTrace( pw );
        } finally {
            if( rsHun2 != null ) {
                try {
                    rsHun2.close();
                } catch( SQLException e ) {
                    bwLogFile.write( "##不成功##\r\n" );
                    bwLogFile.write( "queryData() exception :\r\n" );
                    e.printStackTrace( pw );
                }
            }
            if( rsHun != null ) {
                try {
                    rsHun.close();
                } catch( SQLException e ) {
                    bwLogFile.write( "##不成功##\r\n" );
                    bwLogFile.write( "queryData() exception :\r\n" );
                    e.printStackTrace( pw );
                }
            }
            if( pstHun3 != null ) {
                try {
                    pstHun3.close();
                } catch( SQLException e ) {
                    bwLogFile.write( "##不成功##\r\n" );
                    bwLogFile.write( "queryData() exception :\r\n" );
                    e.printStackTrace( pw );
                }
            }
            if( pstHun2 != null ) {
                try {
                    pstHun2.close();
                } catch( SQLException e ) {
                    bwLogFile.write( "##不成功##\r\n" );
                    bwLogFile.write( "queryData() exception :\r\n" );
                    e.printStackTrace( pw );
                }
            }
            if( pstHun != null ) {
                try {
                    pstHun.close();
                } catch( SQLException e ) {
                    bwLogFile.write( "##不成功##\r\n" );
                    bwLogFile.write( "queryData() exception :\r\n" );
                    e.printStackTrace( pw );
                }
            }
            if( conHun != null ) {
                try {
                    conHun.close();
                } catch( SQLException e ) {
                    bwLogFile.write( "##不成功##\r\n" );
                    bwLogFile.write( "queryData() exception :\r\n" );
                    e.printStackTrace( pw );
                }
            }
        }
    }

    public static void main( String[] args ) throws IOException {
        try {
            // 設定xml
            //localXML = XmlLocalHandlerNewAP.performParser();
            globalXML = XmlGlobalHandlerNewAP.performParser( 81, "" );
            // 設定file
            fLogFile = new File( globalXML.getGlobalTagValue( "apini.logpath" ) + "AP81_" + new SimpleDateFormat( "yyyyMMdd" ).format( new Date() ) + ".log" );
            fLogFile.createNewFile();
            bwLogFile = new BufferedWriter( new FileWriter( fLogFile.getPath(), true ) );
            pw = new PrintWriter( bwLogFile );
            bwLogFile.write( "========== START : " + dateFormat.format( new Date() ) + " ==========\r\n" );
            AP81 newJ81 = new AP81();
            newJ81.queryData();
            bwLogFile.write( "==========  END  : " + dateFormat.format( new Date() ) + " ==========\r\n" );
        } catch( Exception e ) {
            bwLogFile.write( "##不成功##\r\n" );
            bwLogFile.write( "main exception :\r\n" );
            e.printStackTrace( pw );
        } finally {
            // file close
            bwLogFile.close();
            fLogFile = null;
            //localXML = null;
            globalXML = null;
        }
    }
}

package com.ycc.zwc.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.ycc.zwc.ConstantValue;
import com.ycc.zwc.DES;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import sun.security.krb5.internal.crypto.Des;

/**
 * Created by Administrator on 2016/12/9.
 */
public class Entrance extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public Entrance() {
        // TODO Auto-generated constructor stub
    }

    protected void doPost(HttpServletRequest request,final HttpServletResponse response) throws ServletException, IOException {
        System.out.println("get request......");
        request.getInputStream();
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

            parser.parse(request.getInputStream(), new DefaultHandler() {

                private String currentName = "";
                private int currentType = 0;
                private String lotteryId = "";

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    currentName = qName;
                    super.startElement(uri, localName, qName, attributes);
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    if (StringUtils.isNotBlank(currentName)) {
                        if ("body".equals(currentName)) {
                            String bodyDes = new String(ch, start, length);

                            String body = new DES().authcode(bodyDes, "ENCODE", DES.DES_KEY);
                            try {
                                SAXParser bodyParser = SAXParserFactory.newInstance().newSAXParser();

                                bodyParser.parse(new ByteArrayInputStream(body.getBytes("utf-8")), new DefaultHandler() {

                                    @Override
                                    public void characters(char[] ch, int start, int length) throws SAXException {
                                        if ("lotteryid".equalsIgnoreCase(currentName)) {
                                            lotteryId = new String(ch, start, length);
                                        }
                                        super.characters(ch, start, length);
                                    }

                                    @Override
                                    public void startElement(String uri, String localName, String qName, Attributes attributes)
                                            throws SAXException {
                                        currentName = qName;
                                        super.startElement(uri, localName, qName, attributes);
                                    }

                                });

                            } catch (Exception e) {
                                // TODO: handle exception
                            }

                        } else if ("transactiontype".equals(currentName)) {
                            String data = new String(ch, start, length);
                            currentType = Integer.parseInt(data);
                        }
                    }
                    super.characters(ch, start, length);
                }

                @Override
                public void endDocument() throws SAXException {
                    if (currentType != 0) {
                        handlerResponse(currentType, lotteryId, response);
                    }
                    super.endDocument();
                }

            });

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void handlerResponse(int parseInt, String lotteryId, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        System.out.println("transactiontype:" + parseInt);
        try {
            PrintWriter writer = response.getWriter();
            StringBuilder result = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            String temp = "";
            switch (parseInt) {
                case ConstantValue.SALES_INFORMATION:
                    // 玩法可销售期信息
                    String elements = Results.RES_SALES_INFORMATION;
                    if (StringUtils.isNotBlank(lotteryId)) {
                        if (lotteryId.equals(Results.SSQ)) {
                            temp = StringUtils.replaceEach(elements, new String[] { "LID%", "LN%", "I%", "T%" }, new String[] {
                                    lotteryId, "双色球", "2012070", "86400" });
                        } else if (lotteryId.equals(Results.FC3D)) {
                            temp = StringUtils.replaceEach(elements, new String[] { "LID%", "LN%", "I%", "T%" }, new String[] {
                                    lotteryId, "福彩3D", "2012162", "85400" });
                        } else if (lotteryId.equals(Results.QLC)) {
                            temp = StringUtils.replaceEach(elements, new String[] { "LID%", "LN%", "I%", "T%" }, new String[] {
                                    lotteryId, "七乐彩", "2012070", "89400" });
                        }
                    }
                    break;
                case ConstantValue.USER_LOGIN:// 返回成功即可

                    break;
                case ConstantValue.USER_REGISTER:// 用户注册
                    temp = Results.RES_USER_REGISTE;
                    break;
                case ConstantValue.USER_ACCOUNT_DETAIL:
                    // 账户明细
                    temp = Results.RES_USER_ACCOUNT_DETAIL;

                    break;
                case ConstantValue.USER_BETTING:
                    temp = Results.RES_USER_BETTING;
                    break;
            }
            String body = "<body>" + StringUtils.replace(Results.RES_BODY, "E%", temp) + "</body>";

            String md5Info = "20091113101533" + "9ab62a694d8bf6ced1fab6acd48d02f8" + body;

            System.out.println("[" + md5Info + "]");

            result.append("<message version=\"1.0\">").append(
                    StringUtils.replaceEach(Results.RES_HEADER, new String[] { "T%", "D%" }, new String[] { parseInt + "",
                            DigestUtils.md5Hex((md5Info.getBytes("UTF-8"))) }));

            result.append("<body>");
            String info = StringUtils.replace(Results.RES_BODY, "E%", temp);

            // new DES().decrypt(info, DES.DES_KEY);

            String decrypt = new DES().authcode(info, "DECODE", DES.DES_KEY);
            result.append(decrypt);
            result.append("</body>");

            result.append("</message>");
            System.out.println(result.toString());
            writer.write(result.toString());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

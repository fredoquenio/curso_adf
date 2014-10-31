package com.atsistemas.servlet;

import com.atsistemas.utils.ImageUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.*;
import javax.servlet.http.*;

import oracle.jbo.domain.BlobDomain;

public class DownloadImage extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
    private static final long serialVersionUID = 1L;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException,
                                                           IOException {
        response.setContentType(CONTENT_TYPE);

        //get the id parameter passed in the request
        String pathImage = request.getParameter("image");
        OutputStream os = response.getOutputStream();


        File image = ImageUtils.getImage(pathImage);
        if(image != null){
            InputStream isr = new FileInputStream(image);
            BufferedInputStream in = new BufferedInputStream(isr);
            int b;
            byte[] buffer = new byte[10240];
            while ((b = in.read(buffer, 0, 10240)) != -1) {
                os.write(buffer, 0, b);
            }
            os.close();   
        }       
    }
}


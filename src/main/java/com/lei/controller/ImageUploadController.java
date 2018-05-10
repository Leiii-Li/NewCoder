package com.lei.controller;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.*;
import com.lei.util.LoggerUtil;
import com.lei.util.UuidUtil;
import com.lei.util.WendaUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sun.plugin.util.UIUtil;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

/**
 * Created by John on 2018/4/23.
 */
@Controller
public class ImageUploadController {
    private static final String IMAGE_SERVER_URL = "http://oss-cn-shenzhen.aliyuncs.com";
    private static final String AccessKeyId = "LTAIrNO2tB62j3kA";
    private static final String BUCKET_NAME = "alioss-slim";
    private static final String AccessKeySecret = "NJGRZBQWQLoYbmJkuxjp2ASreZagYV";
    private static final String DEFAULT_SUB_FOLDER_FORMAT_AUTO = "yyyyMMddHHmmss";

    @RequestMapping(path = "/uploadImage", method = RequestMethod.POST)
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        LoggerUtil.Logger(" 上传图片");
        try {
            String fileName = file.getOriginalFilename();
            String uploadContentType = file.getContentType();
            String expandedName = "";
            if (uploadContentType.equals("image/pjpeg")
                    || uploadContentType.equals("image/jpeg")) {
                expandedName = ".jpg";
            } else if (uploadContentType.equals("image/png")
                    || uploadContentType.equals("image/x-png")) {
                // IE6上传的png图片的headimageContentType是"image/x-png"
                expandedName = ".png";
            } else if (uploadContentType.equals("image/gif")) {
                expandedName = ".gif";
            } else if (uploadContentType.equals("image/bmp")) {
                expandedName = ".bmp";
            } else {
                return WendaUtil.getJSONString(1, "插入失败");
            }
            String uri = uploadToImageServer(file, uploadContentType);
            return WendaUtil.getJSONString(0, uri);
        } catch (Exception e) {
            e.printStackTrace();
            return WendaUtil.getJSONString(1, "插入失败");
        }
    }

    private String uploadToImageServer(MultipartFile file, String type) throws IOException {
        String uuid = UuidUtil.get32UUID() + ".png";
        OSSClient client = new OSSClient(IMAGE_SERVER_URL, AccessKeyId, AccessKeySecret);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType(type);
        PutObjectResult putObjectResult = client.putObject(BUCKET_NAME, uuid, new ByteArrayInputStream(file.getBytes()), meta);
        URL url = client.generatePresignedUrl(BUCKET_NAME, uuid, new Date());
        String uri = url.toString();
        uri = uri.substring(0, uri.lastIndexOf("?"));
        return uri;
    }
}

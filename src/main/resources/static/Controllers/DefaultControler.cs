public class DefaultController : Controller
{
    [HttpPost]
    public string SaveDetailImg(HttpPostedFileBase files) 
    {
        string returnStr = "";
        if (files == null)
        {
            returnStr = "false,请先选择要上传的图片！";
        }
        else
        {
            string fileName = Path.GetFileName(files.FileName);
            string suffix = fileName.Substring(fileName.LastIndexOf(".") + 1).ToLower(); /* 获取后缀名并转为小写：jpg */
            if (suffix != "jpg" && suffix != "jpeg" && suffix != "bmp" && suffix != "png")
            {
                returnStr = "false,只支持上传后缀为jpg、jpeg、png或bmp的图片！";
            }
            else
            {
                string file = "/Image/"; //图片将要保存的文件夹
                string filePhysicalPath = Server.MapPath("~/" + file);
                if (!Directory.Exists(filePhysicalPath))
                {
                    Directory.CreateDirectory(filePhysicalPath);
                }
                string ImgName = DateTime.Now.ToString("yyyyMMddHHmmssfff") + "." + suffix;
                files.SaveAs(filePhysicalPath + ImgName);

                string url = file + ImgName;

                returnStr = "true," + url;
            }
        }
        return returnStr;
    }
}
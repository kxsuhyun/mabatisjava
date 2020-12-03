package action.member;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;

import action.Action;
import action.ActionForward;
/*
1. �̹��� ���� ���ε�. request ��ü ���Ұ�
	�̹������� ���ε� ��ġ

*/
public class PictureAction implements Action{
	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String path=request.getServletContext().getRealPath("")+"model2/member/picture/";
		String fname=null;
		File f=new File(path);
		if(!f.exists()){
			f.mkdirs();
		}
		//���ε� ����
		MultipartRequest multi=new MultipartRequest(request, path, 10*1024*1024,"UTF-8");
		//fname:<input type="file" name="picture">������ ������ �̸�
//				���ε�� ������ �̸�
		fname=multi.getFilesystemName("picture");
		//new File(path+fname):���ε�� ������ ���� ���.
		//bi: �̹��� ������ �о �޸𸮿� ����
		BufferedImage bi=ImageIO.read(new File(path+fname));
		int width=bi.getWidth()/3; //�����̹��� ����ũ���� 3����1
		int height=bi.getHeight()/3; //�����̹��� ����ũ���� 3���� 1
		//�������̹����� �׸��� ���� ��ȭ��
		BufferedImage thumb=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		//g: �׸��� ����
		Graphics2D g=thumb.createGraphics();
		g.drawImage(bi, 0, 0, width, height,null);
		f=new File(path + "sm_"+fname);
		ImageIO.write(thumb, "jpg", f);
		request.setAttribute("fname", fname);
		return new ActionForward();
	}
}

package action.board;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;

import action.ActionForward;
import model.Board;
import model.BoardDao;

public class BoardAction {
	BoardDao dao = new BoardDao();

	public ActionForward hello(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("hello", "Hello World");
		return new ActionForward();
	}

	/*
	 * 1. �Ķ���Ͱ��� model.Board��ü ����. MultipartRequest ��ü ��� 2. �Խù� ��ȣ num �����ϵ� num��
	 * �ִ밪�� ��ȸ. �ִ밪 +1 ��ϵ� �Խù��� ��ȣ. db���� maxnum�� ���ؼ� +1 ������ num�����ϱ� 3. board ������ db��
	 * ����ϱ�. ��ϼ���: list.do������ �̵� ��Ͻ���: �޼��� ���. writeForm.do������ �̵�
	 */
	public ActionForward write(HttpServletRequest request, HttpServletResponse response) {
		String path = request.getServletContext().getRealPath("/") + "model2/board/file/";
		File f = new File(path);
		if (!f.exists())
			f.mkdir();
		MultipartRequest multi;
		try {
			multi = new MultipartRequest(request, path, 10 * 1024 * 1024, "UTF-8");

			Board board = new Board();
			board.setName(multi.getParameter("name"));
			board.setPass(multi.getParameter("pass"));
			board.setSubject(multi.getParameter("subject"));
			board.setContent(multi.getParameter("content"));
			board.setFile1(multi.getFilesystemName("file1"));

			BoardDao dao = new BoardDao();
			// board table���� num�÷��� �ִ밪 ����. �ִ밪�� ���� ��� 0���� ����

			int num = dao.maxnum();
			board.setNum(++num);
			board.setGrp(num);
			if (dao.insert(board)) {
				return new ActionForward(true, "list.do");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("msg", "�Խù� ��� ����");
		request.setAttribute("url", "writeForm.do");
		return new ActionForward(false, "../alert.jsp");
	}

	/*
	 * 1. ���������� 10���� �Խù��� ����ϱ� pageNum�Ķ���� ���� ���� => ���� ���� 1�� �����ϱ� 2. �ֱ� ��ϵ� �Խù��� ����
	 * ���� ��ġ�� 3. db���� �ش� �������� ��µ� ������ ��ȸ�Ͽ� ȭ�鿡 ��� �Խù��� ��ºκ� ������ ���� ��ºκ� 4.
	 */
	public ActionForward list(HttpServletRequest request, HttpServletResponse response) {
		int pageNum = 1;
		try {
			pageNum = Integer.parseInt(request.getParameter("pageNum"));
		} catch (NumberFormatException e) {
		}
		int limit = 10;

		int boardcount = dao.boardCount();
		List<Board> list = dao.list(pageNum, limit);
		int maxpage = (int) ((double) boardcount / limit + 0.95);
		int startpage = ((int) (pageNum / 10.0 + 0.9) - 1) * 10 + 1;
		int endpage = startpage + 9;
		if (endpage > maxpage) {
			endpage = maxpage;
		}
		int boardnum = boardcount - (pageNum - 1) * limit;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sf.format(new Date());
		request.setAttribute("boardcount", boardcount);
		request.setAttribute("list", list);
		request.setAttribute("pageNum", pageNum);
		request.setAttribute("maxpage", maxpage);
		request.setAttribute("startpage", startpage);
		request.setAttribute("endpage", endpage);
		request.setAttribute("boardnum", boardnum);
		request.setAttribute("today", today);
		return new ActionForward();

	}

	public ActionForward info(HttpServletRequest request, HttpServletResponse response) {
		int num = Integer.parseInt(request.getParameter("num"));
		Board b = dao.selectOne(num); // num�� �ش��ϴ� �Խù� ��ȸ
		dao.readcntAdd(num); // ��ȸ�� ����
		request.setAttribute("b", b);
		return new ActionForward();
	}

	public ActionForward replyForm(HttpServletRequest request, HttpServletResponse response) {
		int num = Integer.parseInt(request.getParameter("num"));
		Board b = dao.selectOne(num); // num�� �ش��ϴ� �Խù� ��ȸ
		request.setAttribute("b", b);
		return new ActionForward();
	}

	public ActionForward reply(HttpServletRequest request, HttpServletResponse response) {
		Board b = new Board();
		b.setNum(Integer.parseInt(request.getParameter("num")));
		b.setName(request.getParameter("name"));
		b.setPass(request.getParameter("pass"));
		b.setSubject(request.getParameter("subject"));
		b.setContent(request.getParameter("content"));
		b.setGrp(Integer.parseInt(request.getParameter("grp")));
		b.setGrplevel(Integer.parseInt(request.getParameter("grplevel")));
		b.setGrpstep(Integer.parseInt(request.getParameter("grpstep")));
		dao.grpStepAdd(b.getGrp(), b.getGrpstep());
		int grplevel = b.getGrplevel();
		int grpstep = b.getGrpstep();
		int num = dao.maxnum();
		String msg = "�亯��Ͻ� ���� �߻�";
		String url = "replyForm.do?num=" + b.getNum();
		b.setNum(++num);
		b.setGrplevel(grplevel + 1);
		b.setGrpstep(grpstep + 1);
		if (dao.insert(b)) {
			msg = "�亯��� �Ϸ�";
			url = "list.do";
		}
		request.setAttribute("msg", msg);
		request.setAttribute("url", url);
		return new ActionForward(false, "../alert.jsp");
	}

	public ActionForward update(HttpServletRequest request, HttpServletResponse response) {
		Board board = new Board();
		String path = request.getServletContext().getRealPath("/") + "model2/board/file/";
		String msg = null;
		String url = null;
		try {
			MultipartRequest multi = new MultipartRequest(request, path, 10 * 1024 * 1024, "UTF-8");
			board.setNum(Integer.parseInt(multi.getParameter("num")));
			board.setName(multi.getParameter("name"));
			board.setPass(multi.getParameter("pass"));
			board.setSubject(multi.getParameter("subject"));
			board.setContent(multi.getParameter("content"));
			board.setFile1(multi.getFilesystemName("file1"));
			// ���Ϻκ��� ������ ���°�� => ������������ ����.
			if (board.getFile1() == null || board.getFile1().equals("")) {
				board.setFile1(multi.getParameter("file2"));
			}
			// 2. ��й�ȣ ����.
			BoardDao dao = new BoardDao();
			Board dbBoard = dao.selectOne(board.getNum());
			msg = "��й�ȣ�� Ʋ�Ƚ��ϴ�.";
			url = "updateForm.do?num=" + board.getNum();
			// board.getPass(): �Էµ� ��й�ȣ
			// dbBoard.getPass(): DB�� ����� ��й�ȣ
			if (board.getPass().equals(dbBoard.getPass())) {
				if (dao.update(board)) { // db�� �Խù� ����
					msg = "�Խñ� ���� �Ϸ�";
					url = "info.do?num=" + board.getNum();
				} else {
					msg = "�Խù� ���� ����";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("msg", msg);
		request.setAttribute("url", url);
		return new ActionForward(false, "../alert.jsp");
	}

	public ActionForward delete(HttpServletRequest request, HttpServletResponse response) {
		int num=Integer.parseInt(request.getParameter("num"));
		String pass= request.getParameter("pass");

		String msg="�Խñ��� ��й�ȣ�� Ʋ�Ƚ��ϴ�";
		String url="deleteForm.do?num="+num;
		BoardDao dao=new BoardDao();
		Board board=dao.selectOne(num);
		if(board==null){
			msg="���� �Խñ� �Դϴ�.";
			url="list.do";
		}else{
			//pass: �Էµ� ��й�ȣ
			//board.getPass(): db�� ��ϵ� ��й�ȣ
			if(pass.equals(board.getPass())){
				if(dao.delete(num)){
					msg="�Խñ� ��������";
					url="list.do";
				}else{
					msg="�Խñ� ���� ����";
					url="info.do?num="+num;
				}
			}
		}
		request.setAttribute("msg", msg);
		request.setAttribute("url", url);
		return new ActionForward(false, "../alert.jsp");
	}
}

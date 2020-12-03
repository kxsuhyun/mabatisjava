package action.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import action.ActionForward;
import model.Member;
import model.MemberDao;
/*
1. id�Ķ���� ���� ��ȸ
2. login���� ����
	�α��λ���:'�α��� �ϼ���' �޼��� ����� loginForm.me
3. login ���� ���� 2
	id �Ķ���Ͱ��� login id�� �ٸ����: �� ���� ��ȸ�� �����մϴ�. �޼��� ���. main.me ���� �̵�
	
	InfoAction
	4.db���� id������ ������ ��ȸ.: selectOne(id)
	5. ��ȸ�� ������ ȭ�� ����ϱ�

*/
public class InfoAction extends UserLoginAction{
	@Override
	public ActionForward doExecute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Member mem=new MemberDao().selectOne(id);
		request.setAttribute("mem", mem);
		return new ActionForward();
	}

}

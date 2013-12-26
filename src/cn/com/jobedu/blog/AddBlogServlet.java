package cn.com.jobedu.blog;

import java.io.IOException;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;

public class AddBlogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 解决从JSP页面接受中文参数乱码
		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			response.sendRedirect("/blog/admin/login.jsp");
		} else {

			// 接受JSP页面传递过来的，与博文有关的3个参数：主题、内容和所属分类的编号
			String title = request.getParameter("title");
			String content = request.getParameter("content");
			String categoryId = request.getParameter("category");

			// 数据源对象可以理解为连接池的管理者，通过他可以获取数据库的连接
			DataSource ds = null;

			try {
				// 通过在context.xml文件，设定的数据源对象的名字，获取数据源对象
				Context context = new InitialContext();
				ds = (DataSource) context.lookup("java:/comp/env/jdbc/mysqlds");
			} catch (Exception e) {
				System.out.println("获取数据源时出错");
			}
			int result = 0;

			try {
				// 添加博文的SQL语句，now()生成当前系统时间
				String sql = "insert into blog (title,content,category_id,createdtime) values (?,?,?,now())";
				// 为SQL语句中的？设定参数
				String params[] = { title, content, categoryId };
				// DButils中核心类，生成对象时传递数据源对象
				QueryRunner qr = new QueryRunner(ds);
				// 调用它的update，完成SQL的运行。其他使用update方法的SQL语句：insert
				// into/update/delete
				result = qr.update(sql, params); // query()
			} catch (SQLException e) {
				e.printStackTrace();
			}

			String message = "";
			if (result == 1) {
				message = "添加博文成功！";
			} else {
				message = "添加博文失败！";
			}

			request.setAttribute("message", message);
			request.getRequestDispatcher("/admin/result.jsp").forward(request,
					response);
		}
	}
}
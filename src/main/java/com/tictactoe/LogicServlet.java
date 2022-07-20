package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int index = getSelectedIndex(req);


        HttpSession currentSession = req.getSession();

        Field field = extractField(currentSession);

        Sign sign = field.getField().get(index);

        if (Sign.EMPTY!=sign){
            getServletContext().getRequestDispatcher("index.jsp").forward(req, resp);
            return;
        }

        field.getField().put(index, Sign.CROSS);
        if (checkWin(resp, currentSession, field))
            return;

        int emptyFieldIndex = field.getEmptyFieldIndex();
        if(emptyFieldIndex>=0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            if (checkWin(resp, currentSession, field))
                return;
        }else {
            currentSession.setAttribute("draw", true);
            currentSession.setAttribute("data", field.getFieldData());
            resp.sendRedirect("/index.jsp");
            return;
        }

        List<Sign> data = field.getFieldData();

        currentSession.setAttribute("field", field);
        currentSession.setAttribute("data", data);

        resp.sendRedirect("/index.jsp");
    }

    private boolean checkWin(HttpServletResponse resp, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if(Sign.CROSS==winner || Sign.NOUGHT==winner) {
            currentSession.setAttribute("winner", winner);
            List<Sign> data = field.getFieldData();

            currentSession.setAttribute("data", data);
            resp.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }

    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if(Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return  (Field) fieldAttribute;
    }

    private int getSelectedIndex(HttpServletRequest req) {
        String click = req.getParameter("click");
        return click.chars().allMatch(Character::isDigit)?Integer.parseInt(click):0;
    }
}

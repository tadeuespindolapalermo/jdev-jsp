package util;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ObjectUtil {
	
	private ObjectUtil () { }
	
	public static boolean isObjectValid(Object object) {
		return nonNull(object) && !object.toString().isEmpty();
	}
	
	public static boolean isObjectsValid(Object... objects) {
		for (Object obj : objects) {
			if (isNull(obj) || obj.toString().isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isObjectsNotValid(Object... objects) {
		return !isObjectsValid(objects);
	}
	
	public static void redirect(HttpServletRequest request, HttpServletResponse response, String path, Map<String, Object> attributes)
			throws ServletException, IOException {
		attributes.entrySet().forEach(att -> request.setAttribute(att.getKey(), att.getValue()));		
		request.getRequestDispatcher(path).forward(request, response);
	}

}

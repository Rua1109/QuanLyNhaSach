<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resources/taglib.jsp" %>
<%@ include file="/resources/admin/template/header.jsp" %>
<%@ include file="/resources/admin/template/sidebar.jsp" %>
<%@ include file="/resources/admin/template/topbar.jsp" %>

<!-- 	========================================================start content======================================================================= -->
	<div class="content-wrapper">
    <div class="container-fluid">
<!--     	================================================================= message =========================================================== -->
		<c:if test="${message != null}">
			<c:choose>
				<c:when test="${fn:contains(message, 'không')}">
					<div class="alert alert-danger" role="alert"><i class="zmdi zmdi-case-check"></i>${message }</div>
					<!-- <script type="text/javascript">
						alert("${message}");
					</script> -->
				</c:when>
				<c:otherwise>
					<div class="alert alert-success" role="alert"><i class="zmdi zmdi-alert-triangle"></i>${message }</div>
				</c:otherwise>
			</c:choose>
		</c:if>
<!-- 		================================================================ create button ===================================================== -->
		
<!--     	=================================================================== table ==================================================================== -->
		<form action="admin/chi-tiet-mat-hang.htm" method="post" enctype="multipart/form-data">
			<div class="card border-success mb-3" style="max-width: 60%; margin: 0 auto; padding: 50px;">
			  	<div class="card-header bg-transparent border-primary"><h3>Cập nhật hình ảnh cho mặt hàng: ${matHang.tenMatHang } - ${matHang.maMatHang }</h3></div>
			  	<div class="card-body text-primary">
			    	<div class="mb-3">
					  	<label for="formFile" class="form-label">Hình ảnh </label>
					  	<input class="form-control" type="file" id="formFile" name="photo">
					</div>
			  	</div>
			  	<button name="uploadPhotoId" value="${matHang.maMatHang }"  type="submit" class="btn btn-light btn-round px-5">
                	Cập nhập
                </button>
			</div>
		</form>
    </div>
    <!-- End container-fluid-->
    </div><!--End content-wrapper-->
<%@ include file="/resources/admin/template/footer.jsp" %>
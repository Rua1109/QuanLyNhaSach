package com.nhasachphuongnam.controller.admin;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.nhasachphuongnam.model.Login;
import com.nhasachphuongnam.model.PersonalInfo;
import com.nhasachphuongnam.model.RoleDTO;
import com.nhasachphuongnam.service.LoginService;
import com.nhasachphuongnam.service.PIService;
import com.nhasachphuongnam.service.RoleService;
import com.nhasachphuongnam.tools.EncryptSHA1;

@Controller
@RequestMapping("admin/thong-tin-ca-nhan/")
public class InfoController {
	
	EncryptSHA1 encrypt = new EncryptSHA1();
	
	@Autowired(required = true)
	PIService piService;
	
	@Autowired(required = true)
	LoginService loginService;
	
	@Autowired
	RoleService roleService;

	@ModelAttribute("roles")
	public List<RoleDTO> getAll() {
		List<RoleDTO> res = roleService.getAll();
		return res;
	}
	
	@ModelAttribute("thongTinCaNhan")
	public PersonalInfo thongTinCaNhan(@ModelAttribute("user") PersonalInfo info) {
		return info;
	}
	
	/*
	 * @ModelAttribute("danhSachDonHang") public List<>
	 */
	
	@RequestMapping("index")
	public String index() {
		return "admin/info/index";
	}
	
	@RequestMapping(value="cap-nhat-hinh-anh/{id}", method=RequestMethod.GET)
	public String uploadPhotoGET(ModelMap model,
			@PathVariable("id") String id) {
		model.addAttribute("ma", id);
		return "admin/info/uploadPhoto";
	}
	
	@RequestMapping(value="cap-nhat-hinh-anh/{id}", method=RequestMethod.POST)
	public String uploadPhotoPOST(ModelMap model,
			@PathVariable("id") String id,
			@RequestParam("photo") MultipartFile file) {
		byte[] image = null;
		try {
			image = file.getBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}
		PersonalInfo pi = piService.getByID(id);
		pi.setHinhAnh(image);
		if (piService.update(pi))
			model.addAttribute("message", "C???p nh???t h??nh ???nh th??nh c??ng");
		else
			model.addAttribute("message", "C???p nh???t h??nh ???nh kh??ng th??nh c??ng");
		
		model.addAttribute("thongTinCaNhan", piService.getByID(id));
		return "admin/info/index";
	}
	
	@RequestMapping(value = "thay-doi-mat-khau", method = RequestMethod.GET)
	public String updatePasswordGET() {
		return "admin/info/changepassword";
	}

	@RequestMapping(value = "/thay-doi-mat-khau", method = RequestMethod.POST)
	public String updatePassword(ModelMap model,
			@ModelAttribute("user") PersonalInfo pi,
			@RequestParam("password") String password,
			@RequestParam("passwordnew") String passwordNew,
			@RequestParam("passwordconfirm") String passwordConfirm) {
		Login login = loginService.getByID(pi.getUsername());
		if (password.trim().length() == 0) {
			model.addAttribute("message", "M???t kh???u kh??ng ???????c ????? tr???ng!");
			return "admin/info/changepassword";
		} else if(passwordNew.trim().length() == 0) {
			model.addAttribute("message", "M???t kh???u m???i kh??ng ???????c ????? tr???ng!");
			return "admin/info/changepassword";
		} else if(!passwordConfirm.equals(passwordNew)) {
			model.addAttribute("message", "M???t kh???u x??c nh???n kh??ng tr??ng kh???p!");
			return "admin/info/changepassword";
		} else if(!login.getPassword().equals(encrypt.encrypt(password))) {
			model.addAttribute("message", "M???t kh???u c?? kh??ng ch??nh x??c!");
			return "admin/info/changepassword";
		} else {
			login.setPassword(passwordConfirm);
			if (loginService.update(login))
				model.addAttribute("message", "C???p nh???t m???t kh???u th??nh c??ng!");
			else
				model.addAttribute("message", "C???p nh???t m???t kh???u kh??ng th??nh c??ng!");
		}
		return "admin/info/index";
	}
	
	@RequestMapping(value = "chinh-sua-thong-tin-ca-nhan", method = RequestMethod.POST)
	public String updatePOST(ModelMap model,
			@ModelAttribute("user") PersonalInfo nhanVien,
			@ModelAttribute("thongTinCaNhan") PersonalInfo pi,
			BindingResult errors) {
		PersonalInfo temp = piService.getByID(nhanVien.getMa());
		if(pi.getTen().trim().length() == 0) {
			errors.rejectValue("ten", "thongTinCaNhan", "T??n kh??ng ???????c ????? tr???ng");
		} else {
			temp.setTen(pi.getTen());
			temp.setSoDienThoai(pi.getSoDienThoai());
			temp.setDiaChi(pi.getDiaChi());
			temp.setNgaySinh(pi.getNgaySinh());
			if(piService.update(temp))
				model.addAttribute("message", "C???p nh???t th??ng tin c?? nh??n th??nh c??ng!");
			else 
				model.addAttribute("message", "C???p nh???t th??ng tin c?? nh??n kh??ng th??nh c??ng!");
		}
		return "admin/info/index";
	}
	
}

package com.nhasachphuongnam.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nhasachphuongnam.dao.CtHoaDonDAO;
import com.nhasachphuongnam.dao.HoaDonDAO;
import com.nhasachphuongnam.dao.KhachHangDAO;
import com.nhasachphuongnam.dao.MatHangDAO;
import com.nhasachphuongnam.dao.NhanVienDAO;
import com.nhasachphuongnam.dao.PhieuNhapDAO;
import com.nhasachphuongnam.entity.CtHoaDon;
import com.nhasachphuongnam.entity.CtHoaDonPK;
import com.nhasachphuongnam.entity.HoaDon;
import com.nhasachphuongnam.entity.KhachHang;
import com.nhasachphuongnam.entity.NhanVien;
import com.nhasachphuongnam.model.ExportOrder;
import com.nhasachphuongnam.model.Product;
import com.nhasachphuongnam.model.ProductDetail;
import com.nhasachphuongnam.service.ExportOrderService;

@Repository
@Transactional
public class ExportOrderServiceImpl implements ExportOrderService{
	
	@Autowired
	MatHangDAO matHangDAO;

	@Autowired
	HoaDonDAO hoaDonDAO;
	
	@Autowired
	PhieuNhapDAO phieuNhapDAO;
	
	@Autowired
	KhachHangDAO khachHangDAO;
	
	@Autowired
	NhanVienDAO nhanVienDAO;
	
	@Autowired
	CtHoaDonDAO ctHoaDonDAO;
	
	public String theNextID() {
		String ma = hoaDonDAO.getLastMa();
		if(ma == null) {
			return "HD00000001";
		}
		int index = Integer.parseInt(ma.substring(2, ma.length())) + 1;
		StringBuilder newmaMH = new StringBuilder("HD");
		for(int i = 0; i < 8 - String.valueOf(index).length(); i++) {
			newmaMH.append('0');
		}
		newmaMH.append(index);
		return newmaMH.toString();
	}
	
	public ExportOrder convert(HoaDon var) {
		ExportOrder res = new ExportOrder();
		if(var.getKhachHang() != null) {
			res.setMaKhachHang(var.getKhachHang().getMaKH());
		}
		if(var.getNhanVien() != null) {
			res.setMaNhanVien(var.getNhanVien().getMaNV());
		}
		res.setDiaChi(var.getDiaChi());
		res.setSdt(var.getSdt());
		res.setMaDonHang(var.getMaHD());
		res.setThoiGian(var.getThoiGian().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		res.setTinhTrang(var.getTinhTrang());
		res.setGiamGia(var.getGiamGia());
		List<ProductDetail> temp2 = new ArrayList<ProductDetail>();
		ProductDetail temp3;
		List<CtHoaDon> temp4 = ctHoaDonDAO.getbyMaHD(var.getMaHD());
		for(CtHoaDon i: temp4) {
			temp3 = new ProductDetail();
			temp3.setMaMatHang(i.getMatHang().getMaMH());
			temp3.setTenMatHang(i.getMatHang().getTenMH());
			temp3.setSoLuong(i.getSoLuong());
			temp3.setGia(i.getGia().longValue());
			/* temp3.setGiamGia(i.getGiamgia()); */
			temp2.add(temp3);
		}
		res.setChiTiets(temp2);
		return res;
	}
	

	public HoaDon convert(ExportOrder var) {
		HoaDon temp1 = hoaDonDAO.getByID(var.getMaDonHang());
		if (temp1 == null)
			temp1 = new HoaDon();
		temp1.setDiaChi(var.getDiaChi());
		if(var.getMaKhachHang() != null) {
			KhachHang temp2 = khachHangDAO.getByID(var.getMaKhachHang());
			temp1.setKhachHang(temp2);
		}
		if(var.getMaNhanVien() != null) {
			NhanVien temp3 = nhanVienDAO.getByID(var.getMaNhanVien());
			temp1.setNhanVien(temp3);
		}
		temp1.setSdt(var.getSdt());
		temp1.setThoiGian(Date.from(var.getThoiGian().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		temp1.setGiamGia(var.getGiamGia());
		temp1.setTinhTrang(var.getTinhTrang());
		return temp1;
	}

	public String add(ExportOrder var) {
		HoaDon temp1 = new HoaDon();	//t???a HoaDon m???i
		temp1.setMaHD(this.theNextID());	//Th??m m?? ????n h??ng cho h??a ????n v???a t???o
		if(var.getMaKhachHang() == null) {
			temp1.setKhachHang(null);
		} else {
			try {
				//c???p nh???t m?? kh??ch h??ng cho ????n h??ng
				temp1.setKhachHang(khachHangDAO.getByID(var.getMaKhachHang()));	
			} catch (Exception e) {	//ki???m tra null
				e.printStackTrace();
				return null;
			}
		}
		if(var.getMaNhanVien() == null) {
			temp1.setNhanVien(null);
		} else {
			try {
				temp1.setNhanVien(nhanVienDAO.getByID(var.getMaNhanVien()));
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		temp1.setSdt(var.getSdt());		//th??m s??? ??i???n tho???i cho ????n h??ng
		//th??m th??ng tin th???i gian cho ????n h??ng v?? convert th???i gian t??? LocalDate sang Date ????? ph?? h???p v???i jpa hibernate 4
		temp1.setThoiGian(Date.from(var.getThoiGian().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		temp1.setGiamGia(var.getGiamGia());		//th??m th??ng tin gi???m gi?? cho ????n h??ng
		temp1.setTinhTrang(var.getTinhTrang());	//Chuy???n ????n h??ng sang tr???ng th??i ?????t h??ng
		temp1.setDiaChi(var.getDiaChi());	//th??m th??ng tin ?????a ch??? nh???n h??ng
		List<CtHoaDon> CTHoaDons = new ArrayList<CtHoaDon>();
		CtHoaDon temp2;
		//th??m CtHoaDon v??o trong ????n h??ng
		for(ProductDetail i: var.getChiTiets()) {
			CtHoaDonPK pk = new CtHoaDonPK();	//kh??a ch??nh c???a CtHoaDon
			pk.setMaHD(temp1.getMaHD());
			pk.setMaMH(i.getMaMatHang());
			temp2 = new CtHoaDon();	//CtHoaDon
			temp2.setId(pk);	//set kh??a ch??nh cho CtHoaDon
			temp2.setSoLuong(i.getSoLuong());	//set s??? l?????ng cho CtHoaDon
			temp2.setGia(BigDecimal.valueOf(i.getGia()));	//set gi?? ti???n cho CtHoaDon
			//thay ?????i s??? l?????ng c???a m???t h??ng
			matHangDAO.changeSoLuong(i.getMaMatHang(), 0 - i.getSoLuong());
			CTHoaDons.add(temp2);	//Th??m CtHoaDon v??o trong ????n h??ng
		}
		temp1.setCtHoaDons(CTHoaDons);	//Th??m CtHoaDon v??o trong ????n h??ng
		if(hoaDonDAO.add(temp1))	//Th??m ????n h??ng
			return temp1.getMaHD();		//tr??? v??? m?? ????n h??ng c???a ????n h??ng v???a t???o
		return null;
	}
	
	public boolean update(ExportOrder var) {
		HoaDon temp1 = this.convert(var);
		if(hoaDonDAO.update(temp1))
			return true;
		return false;
	}
	
	public boolean delete(String ma) {
		List<CtHoaDon> ctHoaDons = ctHoaDonDAO.getbyMaHD(ma);
		if(hoaDonDAO.delete(ma)) {
			for(CtHoaDon i: ctHoaDons) {
				matHangDAO.changeSoLuong(i.getMatHang().getMaMH(), i.getSoLuong());
			}
			return true;
		}
		return false;
	}
	
	public ExportOrder getByID(String ma) {
		HoaDon temp = hoaDonDAO.getByID(ma);
		if(temp == null) {
			return null;
		}
		ExportOrder res = convert(temp);
		return res;
	}
	
	public List<ExportOrder> getAll(){
		List<ExportOrder> res = new ArrayList<ExportOrder>();
		List<HoaDon> hoaDons = hoaDonDAO.getAll();
		for(HoaDon i: hoaDons) {
			res.add(convert(i));
		}
		return res;
	}
	
	public List<ExportOrder> getAllByCustomerID(String id){
		List<ExportOrder> res = new ArrayList<ExportOrder>();
		List<HoaDon> temp = hoaDonDAO.getHoaDonByMaKhachHang(id);
		for(HoaDon i: temp) {
			res.add(convert(i));
		}
		return res;
	}
	
	public List<ExportOrder> getAllByStaffID(String id){
		List<ExportOrder> res = new ArrayList<ExportOrder>();
		List<HoaDon> temp = hoaDonDAO.getHoaDonByMaNhanVien(id);
		for(HoaDon i: temp) {
			res.add(convert(i));
		}
		return res;
	}
	
	public List<ExportOrder> getAllBetweenDate(LocalDate start, LocalDate end) {
		List<ExportOrder>  res = new ArrayList<ExportOrder>();	//t???o danh s??ch ????n h??ng
		//L???y danh s??ch c??c ????n h??ng c?? trong kho???ng th???i gian cho nh???p v??o ?????ng th???i chuy???n ?????i ki???u d??? li???u t??? LocalDate sang Date cho ph?? h???p
		List<HoaDon> temp = hoaDonDAO.getBetweenThoiGian(Date.from(start.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), Date.from(end.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		for(HoaDon i: temp) {
			res.add(convert(i));	//th??m c??c ????n h??ng v???a l???y ???????c v??o danh s??ch c??c ????n h??ng tr??? v???
		}
		return res;	//tr??? v??? danh s??ch c??ch ????n h??ng v???a l???y ???????c
	}


	public List<ExportOrder> getDanhSachUserOrder(){
		List<ExportOrder> res = new ArrayList<ExportOrder>();
		List<HoaDon> hoaDons = hoaDonDAO.getAll();
		for(HoaDon i: hoaDons) {
			if(i.getTinhTrang().equals("1")) {
				res.add(convert(i));
			}
		}
		return res;
	}
	
	public List<ExportOrder> getDanhSachUserDeliveryOrder(){
		List<ExportOrder> res = new ArrayList<ExportOrder>();
		List<HoaDon> hoaDons = hoaDonDAO.getAll();
		for(HoaDon i: hoaDons) {
			if(i.getTinhTrang().equals("2")) {
				res.add(convert(i));
			}
		}
		return res;
	}
	
	public List<Product> getDanhSachMatHangByExportOrderID(String ma){
		List<Product> res = new ArrayList<>();
		List<CtHoaDon> temp = ctHoaDonDAO.getbyMaHD(ma);
		Product temp2;
		if(temp != null) {
			for(CtHoaDon i: temp) {
				temp2 = new Product(i.getMatHang().getMaMH(), i.getMatHang().getTenMH(), i.getMatHang().getHinhAnh(), i.getSoLuong(), "", "", i.getMatHang().getAllow(), i.getGia().longValue(), Float.valueOf(0), i.getMatHang().getLoaiMatHang().getMaLoai());
				res.add(temp2);
			}
		}
		return res;
	}
	
	public String confirmUserOrder(String ma, String maNhanVien) {
		HoaDon temp = hoaDonDAO.getByID(ma);
		NhanVien temp2 = nhanVienDAO.getByID(maNhanVien);
		if(temp == null || temp2 == null) {
			return null;
		}
		temp.setNhanVien(temp2);
		temp.setTinhTrang("2");
		if(hoaDonDAO.update(temp)) {
			return ma;
		}
		return null;
	}
	
	public String comfirmUserDeliveryOrder(String ma) {
		HoaDon temp = hoaDonDAO.getByID(ma);
		if(temp == null) {
			return null;	
		}
		temp.setTinhTrang("3");
		if(hoaDonDAO.update(temp)) {
			return ma;
		}
		return null;
	}
}

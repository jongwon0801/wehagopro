package com.dozone.wehagopro.controller;

import com.dozone.wehagopro.domain.*;
import com.dozone.wehagopro.service.OrganizationService;
import com.dozone.wehagopro.service.common.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class OrganizationController {

    @Autowired
    OrganizationService organizationService;
    @Autowired
    MailService mailService;

    // 조직도 목록
    @GetMapping("/showMyWorkPlace")
    public List<OrganizationInitCompDTO> showMyWorkPlace(Integer t_user_no){
        return organizationService.showMyWorkPlace(t_user_no);
    }

    // 조직도 회사 정보
    @GetMapping("/showMyCompanyInfo")
    public List<OrganizationCompInfoDTO> showMyCompanyInfo(Integer t_user_no){
        return organizationService.showMyCompanyInfo(t_user_no);
    }

    // 조직도 직원 상태
    @GetMapping("/showMyEmployeeState")
    public OrganizationInitEmplDTO showMyEmployeeState(Integer pk, Integer index){
        return organizationService.showMyEmployeeState(pk, index);
    }

    // 조직도 직원 목록
    @GetMapping("/showMyEmployees")
    public List<OrganizationEmplInfoDTO> showMyEmployees(@RequestParam("nodeName") String nodeName, @RequestParam("pk") Integer pk, @RequestParam("index") Integer index, @RequestParam("t_employee_state") Integer t_employee_state){
        return organizationService.showMyEmployees(nodeName, pk, index, t_employee_state);
    }

    @GetMapping(value = "/images/{imageName:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public Resource getImage(@PathVariable String imageName) throws IOException {
        System.out.println("요청왔다" + imageName);
        return organizationService.getImage(imageName);
    }

    // 이미지 저장
    @PostMapping("/uploadEmployeePhoto")
    public PhotoDto uploadEmployeePhoto(@RequestParam("file")MultipartFile file){
        return organizationService.uploadEmployeePhoto(file);
    }

    // 직원 등록
    @PostMapping("/makeRoomForANewEmployee")
    public void makeRoomForANewEmployee(@RequestBody OrganizationEmplRegiDTO dto){
        System.out.println("dto : "+dto);
        organizationService.makeRoomForANewEmployee(dto);
    }

    // 조직도 부서 수정
    @PostMapping("/editingOrganization")
    public void editingOrganization(@RequestBody List<OrganizationEditDTO> dto){
        List<OrganizationEditDTO> insertDto = new ArrayList<>();
        List<OrganizationEditDTO> updateDto = new ArrayList<>();
        List<OrganizationEditDTO> deleteDto = new ArrayList<>();

        for(OrganizationEditDTO dt : dto) {
            System.out.println(dt);
            if(dt.getT_organization_name() ==null && dt.getT_company_no() ==null){
                deleteDto.add(dt);
            }else if(dt.getT_organization_no()<0){
                insertDto.add(dt);
            }else if(dt.getT_company_no()!=null && dt.getT_organization_no()>=0){
                updateDto.add(dt);
            }
        }

        organizationService.editingOrganization(insertDto, updateDto, deleteDto);
    }

    // 메일
    @PostMapping("/sendMailToEmployee")
    public void sendMailToEmployee(@RequestBody OrganizationMailDto dto) throws MessagingException {
        System.out.println("Received request data: " + dto.toString());
        System.out.println("Employer: " + dto.getEmployer());
        System.out.println("Checked Employee List: " + dto.getCheckedEmployee());

        for (OrganizationSelectedDto obj : dto.getCheckedEmployee()) {
            System.out.println("Received Object: " + obj.toString());
        }
        mailService.sendMailToEmployee(dto.getEmployer(), dto.getCheckedEmployee());
    }

}

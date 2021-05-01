package es.uji.ei102720mgph.SANA.controller;

import es.uji.ei102720mgph.SANA.dao.MunicipalManagerDao;
import es.uji.ei102720mgph.SANA.dao.MunicipalityDao;
import es.uji.ei102720mgph.SANA.dao.PostalCodeMunicipalityDao;
import es.uji.ei102720mgph.SANA.model.Municipality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/municipality")
public class MunicipalityController {

    private MunicipalityDao municipalityDao;
    private MunicipalManagerDao municipalManagerDao;
    private PostalCodeMunicipalityDao postalCodeMunicipalityDao;

    @Autowired
    public void setMunicipalityDao(MunicipalityDao municipalityDao){
        this.municipalityDao = municipalityDao;
    }

    @Autowired
    public void setMunicipalManagerDao(MunicipalManagerDao municipalManagerDao){
        this.municipalManagerDao = municipalManagerDao;
    }

    @Autowired
    public void setPostalCodeMunicipalityDao(PostalCodeMunicipalityDao postalCodeMunicipalityDao){
        this.postalCodeMunicipalityDao = postalCodeMunicipalityDao;
    }

    @RequestMapping("/list")
    public String listMunicipalities(Model model) {
        model.addAttribute("municipalities", municipalityDao.getMunicipalities());
        return "municipality/list";
    }

    @RequestMapping(value="/get/{name}")
    public String getMunicipality(Model model, @PathVariable("name") String name){
        model.addAttribute("municipality", municipalityDao.getMunicipality(name));
        model.addAttribute("municipalManagers", municipalManagerDao.getManagersOfMunicipality(name));

        //TODO pasarle tmb los cp del municipio

        return "/municipality/get";
    }

    @RequestMapping(value="/add")
    public String addMunicipality(Model model) {
        model.addAttribute("municipality", new Municipality());
        return "municipality/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("municipality") Municipality municipality,
                                   BindingResult bindingResult) {
        MunicipalityValidator municipalityValidator = new MunicipalityValidator();
        municipalityValidator.validate(municipality, bindingResult);

        if (bindingResult.hasErrors())
            return "municipality/add"; //tornem al formulari per a que el corregisca
        municipalityDao.addMunicipality(municipality);
        return "redirect:list"; //redirigim a la lista, post/redirect/get
    }

    @RequestMapping(value="/update/{name}", method = RequestMethod.GET)
    public String editMunicipality(Model model, @PathVariable String name) {
        model.addAttribute("municipality", municipalityDao.getMunicipality(name));
        return "municipality/update";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("municipality") Municipality municipality,
                                      BindingResult bindingResult) {
        MunicipalityValidator municipalityValidator = new MunicipalityValidator();
        municipalityValidator.validate(municipality, bindingResult);

        if (bindingResult.hasErrors())
            return "municipality/update";
        municipalityDao.updateMunicipality(municipality);
        return "redirect:list";
    }

    @RequestMapping(value="/delete/{name}")
    public String processDelete(@PathVariable String name) {
        municipalityDao.deleteMunicipality(name);
        return "redirect:../list";
    }
}

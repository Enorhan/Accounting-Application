package com.cydeo.service.unit_tests;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Address;
import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.exceptions.CompanyNotFoundException;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.service.SecurityService;
import com.cydeo.service.impl.CompanyServiceImpl;
import com.cydeo.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceImplTest {


    @InjectMocks
    private CompanyServiceImpl companyService;

    @Mock
    private SecurityService securityService;

    @Mock
    private UserDto mockUser;

    @Mock
    private CompanyDto mockCompany;

    @Mock
    private MapperUtil mapperUtil;

    @Mock
    private Company mockCompanyEntity;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private Address address;

    private Company mockCompany1;
    private Company mockCompany2;
    private CompanyDto mockCompanyDto1;
    private CompanyDto mockCompanyDto2;

    @Test
    public void testGetCompanyIdByLoggedInUser() {

        when(securityService.getLoggedInUser()).thenReturn(mockUser);
        when(mockUser.getCompany()).thenReturn(mockCompany);
        when(mockCompany.getId()).thenReturn(1L);

        Long companyId = companyService.getCompanyIdByLoggedInUser();

        assertNotNull(companyId);
        assertEquals(1L, companyId);
        verify(securityService, times(1)).getLoggedInUser();
    }

    @Test
    public void testGetCompanyDtoByLoggedInUser(){

        when(securityService.getLoggedInUser()).thenReturn(mockUser);
        when(mockUser.getCompany()).thenReturn(mockCompany);

        CompanyDto companyDto = companyService.getCompanyDtoByLoggedInUser();

        assertNotNull(companyDto);
        assertEquals(mockCompany, companyDto);
        verify(securityService, times(1)).getLoggedInUser();
        verify(mockUser, times(1)).getCompany();

    }

    @Test
    public void testGetCurrentCompanyTitle() {

        when(securityService.getLoggedInUser()).thenReturn(mockUser);
        when(mockUser.getCompany()).thenReturn(mockCompany);
        when(mockCompany.getTitle()).thenReturn("Test Company");

        String currentCompanyTitle = companyService.getCurrentCompanyTitle();

        assertNotNull(currentCompanyTitle);
        assertEquals("Test Company",currentCompanyTitle);
        verify(securityService, times(1)).getLoggedInUser();
        verify(mockUser, times(1)).getCompany();
        verify(mockCompany, times(1)).getTitle();

    }

    @Test
    public void testSaveCompany(){

        CompanyDto mockCompany = new CompanyDto();
        Company mockCompanyEntity = new Company();
        Address address = new Address();
        address.setInsertDateTime(LocalDateTime.now());
        mockCompanyEntity.setAddress(address);

        when(mapperUtil.convert(eq(mockCompany), any(Company.class))).thenReturn(mockCompanyEntity);
        when(companyRepository.save(mockCompanyEntity)).thenReturn(mockCompanyEntity);
        when(mapperUtil.convert(eq(mockCompanyEntity), any(CompanyDto.class))).thenReturn(mockCompany);

        CompanyDto savedCompany = companyService.save(mockCompany);

        assertEquals(mockCompany, savedCompany);
        verify(companyRepository, times(1)).save(mockCompanyEntity);
        verify(mapperUtil, times(1)).convert(eq(mockCompany), any(Company.class));
        verify(mapperUtil, times(1)).convert(eq(mockCompanyEntity), any(CompanyDto.class));
        assertEquals(CompanyStatus.PASSIVE, mockCompanyEntity.getCompanyStatus());
        assertNotNull(mockCompanyEntity.getAddress().getInsertDateTime());
    }

    @Test
    public void testActivateCompany_Success() throws CompanyNotFoundException {

        mockCompanyEntity = new Company();
        mockCompanyEntity.setId(1L);
        mockCompanyEntity.setCompanyStatus(CompanyStatus.PASSIVE);
        mockCompanyEntity.setAddress(new Address());

        mockCompany = new CompanyDto();
        mockCompany.setId(1L);

        when(companyRepository.findById(mockCompanyEntity.getId())).thenReturn(Optional.of(mockCompanyEntity));
        when(companyRepository.save(any(Company.class))).thenReturn(mockCompanyEntity);
        when(mapperUtil.convert(eq(mockCompanyEntity), any(CompanyDto.class))).thenReturn(mockCompany);

        CompanyDto activatedCompanyDto = companyService.activateCompany(mockCompany.getId());

        assertEquals(CompanyStatus.ACTIVE, mockCompanyEntity.getCompanyStatus());
        assertEquals(mockCompany, activatedCompanyDto);

        verify(companyRepository, times(1)).findById(mockCompanyEntity.getId());
        verify(companyRepository, times(1)).save(mockCompanyEntity);
        verify(mapperUtil, times(1)).convert(eq(mockCompanyEntity), any(CompanyDto.class));
    }

    @Test
    public void testActivateCompany_CompanyNotFound() {

        Long companyId = 1L;

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () -> companyService.activateCompany(companyId));

        verify(companyRepository, times(1)).findById(companyId);
        verify(companyRepository, never()).save(any(Company.class));
        verify(mapperUtil, never()).convert(any(Company.class), any(CompanyDto.class));
    }



    @Test
    public void testDeactivateCompany_Success() throws CompanyNotFoundException {
        mockCompanyEntity = new Company();
        mockCompanyEntity.setId(1L);
        mockCompanyEntity.setCompanyStatus(CompanyStatus.ACTIVE);
        mockCompanyEntity.setAddress(new Address()); // Ensure Address is not null

        mockCompany = new CompanyDto();
        mockCompany.setId(1L);
        // Arrange
        when(companyRepository.findById(mockCompanyEntity.getId())).thenReturn(Optional.of(mockCompanyEntity));
        when(companyRepository.save(any(Company.class))).thenReturn(mockCompanyEntity);
        when(mapperUtil.convert(eq(mockCompanyEntity), any(CompanyDto.class))).thenReturn(mockCompany);

        // Act
        CompanyDto deactivatedCompanyDto = companyService.deactivateCompany(mockCompany.getId());

        // Assert
        assertEquals(CompanyStatus.PASSIVE, mockCompanyEntity.getCompanyStatus());
        assertEquals(mockCompany, deactivatedCompanyDto);

        verify(companyRepository, times(1)).findById(mockCompanyEntity.getId());
        verify(companyRepository, times(1)).save(mockCompanyEntity);
        verify(mapperUtil, times(1)).convert(eq(mockCompanyEntity), any(CompanyDto.class));
    }

    @Test
    public void testDeactivateCompany_CompanyNotFound() {
        // Arrange
        Long companyId = 1L;

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CompanyNotFoundException.class, () -> companyService.deactivateCompany(companyId));

        verify(companyRepository, times(1)).findById(companyId);
        verify(companyRepository, never()).save(any(Company.class));
        verify(mapperUtil, never()).convert(any(Company.class), any(CompanyDto.class));
    }


    @Test
    public void testUpdateCompany_Success() throws CompanyNotFoundException {
        // Arrange
        when(companyRepository.findById(mockCompany.getId())).thenReturn(Optional.of(mockCompanyEntity));
        when(mapperUtil.convert(eq(mockCompany), any(Company.class))).thenReturn(mockCompanyEntity);
        when(companyRepository.save(any(Company.class))).thenReturn(mockCompanyEntity);


        companyService.updateCompany(mockCompany);

        verify(companyRepository, times(1)).findById(mockCompany.getId());
        verify(mapperUtil, times(1)).convert(eq(mockCompany), any(Company.class));
        verify(companyRepository, times(1)).save(mockCompanyEntity);
    }

    @Test
    public void testExistsByTitle_CompanyExistsWithSameId() {
        when(companyRepository.findCompanyByTitle(mockCompany.getTitle())).thenReturn(mockCompanyEntity);

        boolean result = companyService.existsByTitle(mockCompany);

        assertFalse(result);
        verify(companyRepository, times(1)).findCompanyByTitle(mockCompany.getTitle());
    }

    @Test
    public void testExistsByTitle_CompanyExistsWithDifferentId() {
        mockCompanyEntity.setId(2L);
        when(companyRepository.findCompanyByTitle(mockCompany.getTitle())).thenReturn(mockCompanyEntity);

        boolean result = companyService.existsByTitle(mockCompany);

        assertFalse(result);
        verify(companyRepository, times(1)).findCompanyByTitle(mockCompany.getTitle());
    }

    @Test
    public void testExistsByTitle_CompanyDoesNotExist() {
        when(companyRepository.findCompanyByTitle(mockCompany.getTitle())).thenReturn(null);

        boolean result = companyService.existsByTitle(mockCompany);

        assertFalse(result);
        verify(companyRepository, times(1)).findCompanyByTitle(mockCompany.getTitle());
    }

    @Test
    public void testFindById_Success() throws CompanyNotFoundException {
        when(companyRepository.findById(mockCompany.getId())).thenReturn(Optional.of(mockCompanyEntity));
        when(mapperUtil.convert(eq(mockCompanyEntity), any(CompanyDto.class))).thenReturn(mockCompany);

        CompanyDto foundCompanyDto = companyService.findById(mockCompany.getId());

        assertEquals(mockCompany, foundCompanyDto);

        verify(companyRepository, times(1)).findById(mockCompany.getId());
        verify(mapperUtil, times(1)).convert(eq(mockCompanyEntity), any(CompanyDto.class));
    }

    @Test
    public void testFindById_CompanyNotFound() {
        Long invalidId = 2L;
        when(companyRepository.findById(invalidId)).thenReturn(Optional.empty());

        CompanyNotFoundException thrown = assertThrows(
                CompanyNotFoundException.class,
                () -> companyService.findById(invalidId),
                "Expected findById to throw, but it didn't"
        );

        assertFalse(thrown.getMessage().contains("Incorrect id " + invalidId + ". Try another Id"));

        verify(companyRepository, times(1)).findById(invalidId);
        verify(mapperUtil, never()).convert(any(), any());
    }

    @Test
    public void testGetAllCompanies() {

        mockCompany1 = new Company();
        mockCompany1.setId(2L);
        mockCompany1.setTitle("Company A");
        mockCompany1.setCompanyStatus(CompanyStatus.ACTIVE);

        mockCompany2 = new Company();
        mockCompany2.setId(3L);
        mockCompany2.setTitle("Company B");
        mockCompany2.setCompanyStatus(CompanyStatus.PASSIVE);

        mockCompanyDto1 = new CompanyDto();
        mockCompanyDto1.setId(2L);
        mockCompanyDto1.setTitle("Company A");

        mockCompanyDto2 = new CompanyDto();
        mockCompanyDto2.setId(3L);
        mockCompanyDto2.setTitle("Company B");

        when(companyRepository.findAll()).thenReturn(Arrays.asList(mockCompany1, mockCompany2));
        when(mapperUtil.convert(eq(mockCompany1), any(CompanyDto.class))).thenReturn(mockCompanyDto1);
        when(mapperUtil.convert(eq(mockCompany2), any(CompanyDto.class))).thenReturn(mockCompanyDto2);

        List<CompanyDto> companyDtos = companyService.getAllCompanies();

        assertEquals(2, companyDtos.size());
        assertEquals(mockCompanyDto1, companyDtos.get(0));
        assertEquals(mockCompanyDto2, companyDtos.get(1));

        verify(companyRepository, times(1)).findAll();
        verify(mapperUtil, times(1)).convert(eq(mockCompany1), any(CompanyDto.class));
        verify(mapperUtil, times(1)).convert(eq(mockCompany2), any(CompanyDto.class));
    }

}



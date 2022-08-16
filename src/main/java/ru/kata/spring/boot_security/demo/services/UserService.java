package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;
import ru.kata.spring.boot_security.demo.roles.Role;
import ru.kata.spring.boot_security.demo.roles.User;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	RoleRepository roleRepo;
	
	@Autowired PasswordEncoder passwordEncoder;

	@Transactional
	public void saveUser(User user, List<Integer> roles) {
		for (Integer i : roles) {
			Role role = roleRepo.findById(i).get();
			user.addRole(role);
		}
		encodePassword(user);
		userRepo.save(user);
	}
	
	public List<User> listAll() {
		return userRepo.findAll();
	}

	public User get(int id) {
		return userRepo.findById(id).get();
	}

	public User findByUsername(String name) {
		return userRepo.findByName(name);
	}

	public Set<Role> findRolesByName(String roleName) {
		Set<Role> roles = new HashSet<>();
		for (Role role : listRoles()) {
			if (roleName.contains(role.getName())) {
				roles.add(role);
			}
		}
		return roles;
	}
	
	public List<Role> listRoles() {
		return roleRepo.findAll();
	}
	
	@Transactional
	public void update(User user) {
		encodePassword(user);
		userRepo.save(user);
	}

	public void deleteById(int id) {
		//User user = get(id);
		userRepo.deleteById(id);
	}
	
	private void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);		
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException(String.format("User '%s' not found", username));
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(),
				user.getPassword(), mapRolesToAuthorities(user.getRoles()));
	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}
	@org.springframework.transaction.annotation.Transactional
	public void updateUser(User user) {
		User userFromDb = userRepo.getById(user.getId());
		if (!userFromDb.getPassword().equals(user.getPassword())) {
			String encodedPassword = passwordEncoder.encode(user.getPassword());
			user.setPassword(encodedPassword);
		}
		userRepo.save(user);
	}
}

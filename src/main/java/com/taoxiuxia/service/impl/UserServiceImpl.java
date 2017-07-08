package com.taoxiuxia.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taoxiuxia.mapper.UserMapper;
import com.taoxiuxia.model.User;
import com.taoxiuxia.service.IUserService;
import com.taoxiuxia.util.PasswordUtil;
import com.taoxiuxia.util.StringTools;

@Service("userService")
public class UserServiceImpl implements IUserService {

	private UserMapper userMapper;

	public UserMapper getUserMapper() {
		return userMapper;
	}

	@Autowired
	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	/**
	 * 注册用户
	 */
	@Override
	public int register(User user) {
		user.setPassword(PasswordUtil.geneMD5WithSalt(user.getPassword()));
		Date curDate = new Date();
		user.setRegisterTime(curDate);
		user.setLastLoginTime(curDate);
		return userMapper.insert(user);
	}

	@Override
	public User login(String account, String password,boolean hasMD5) {
		if (StringTools.isEmpty(account) || StringTools.isEmpty(password)) {
			System.out.println("输入参数不合法");
			return null;
		}
		User user = null;
		if (account.contains("@")) { // 邮箱登录
			user = this.findUserByEmail(account);
		} else { // 用户名登录
			user = this.findUserByUserName(account);
		}
		if (null == user) {
			System.out.println("用户不存在");
			return null;
		}
		if(hasMD5){
			if (password.equals(user.getPassword())) {
				System.out.println("密码错误");
				return null;
			}
		}else{
			if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
				System.out.println("密码错误");
				return null;
			}
		}
		return user;
	}
	
	@Override
	public void update(User user) {
		// TODO Auto-generated method stub
	}
	
	public User findUserByEmail(String email) {
		Map<String,String> map = new HashMap<String,String>(); 
		map.put("email", email);
		if(userMapper==null){
			userMapper = getUserMapper();
		}
		List<User>list = userMapper.findUserByEmail(map);
		System.out.println("findUserByEmail.size ==> "+list.size());
		if(list.size()==1){
			return list.get(0);
		}
		return null;
	}

	public User findUserByUserName(String userName) {
		Map<String,String> map = new HashMap<String,String>(); 
		map.put("name", userName);
		if(userMapper==null){
			System.out.println("userMapper == null");
			userMapper = getUserMapper();
		}
		System.out.println("userMapper ==> "+userMapper);
		List<User>list = userMapper.findUserByUserName(map);
		System.out.println("findUserByUserName.size ==> "+list.size());
		if(list.size()==1){
			return list.get(0);
		}
		return null;
	}
}

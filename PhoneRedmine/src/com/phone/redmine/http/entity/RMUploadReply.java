package com.phone.redmine.http.entity;

public class RMUploadReply
{
	public Token upload;
	
	public static class Token
	{
		public String token;
		public String filename;
		public String content_type;
	}
}


package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
//import com.amazon.speech.ui.SimpleCard;
public class InterviewSpeechlet  implements SpeechletV2{
	private static final Logger log = LoggerFactory.getLogger(InterviewSpeechlet.class);
 //public static Connection con;
  public static String ec2_ip="http://ec2-3-121-215-245.eu-central-1.compute.amazonaws.com/interview";
 public static int counter=0;
 public static String convo="";
 public static String rollno;
 public String answer[];
	@Override
	public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
		log.info("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
				requestEnvelope.getSession().getSessionId());
		// any initialization logic goes here
	}

	@Override
	public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
		log.info("onLaunch requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
				requestEnvelope.getSession().getSessionId());
	
		int random=(int)(Math.random()*100);
		String SpeechOutput;
		random=3;
		if(random%2==0)
		{
		 SpeechOutput=" Welcome to Alexa Interview Skill. Sorry, No interview scheduled now. Thank You.";
		}
		else
		{
		SpeechOutput="Welcome to Alexa Interview Skill. You can say your netra id like, my netra id is sixteen zero five one thirty eight. ";	
		}
		 
		 String reprompt="Dont feel hesitant to ask your query";
		 return newAskResponse(SpeechOutput,reprompt);
		 
		}
         
	@Override
	public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
		IntentRequest request = requestEnvelope.getRequest();
		log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
				requestEnvelope.getSession().getSessionId());

		Intent intent = request.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;
		Session session = requestEnvelope.getSession();
		if("AuthenticateIntent".equals(intentName))
		{
			return startInterview(intent,session);
		}
		else if("RepeatQuestionIntent".equals(intentName))
		{
			return repeat(intent,session);
		}
		else if("YESIntent".equals(intentName))
		{
			return yesMethod(intent,session);
		}
		else if("AnswerIntent".equals(intentName))
			return answerMethod(intent,session);
		else if("SkipIntent".equals(intentName))
			return skipMethod(intent,session);
		else if ("AMAZON.HelpIntent".equals(intentName)) {
			return getHelp();
		} else if ("AMAZON.StopIntent".equals(intentName)) {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Goodbye");

			return SpeechletResponse.newTellResponse(outputSpeech);
		} else if ("AMAZON.CancelIntent".equals(intentName)) {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Goodbye");

			return SpeechletResponse.newTellResponse(outputSpeech);
		} else {
			String errorSpeech = "This is unsupported.  Please try something else.";
			return newAskResponse(errorSpeech, errorSpeech);
		}
		}
	private SpeechletResponse getHelp() {
		String speechOutput = "you can say next question for the next question ,to repeat the question say repeat,to quit the interview say end interview";
		String repromptText = "you can say next question for the next question ,to repeat the question say repeat,to quit the interview say end interview";
		return newAskResponse(speechOutput, repromptText);
	}
	private SpeechletResponse skipMethod(Intent intent,final Session session)
	{
		int totalquestions = (int) session.getAttribute("totalquestions");int qcount=(int)session.getAttribute("qcount");
		String resString = null;
		// check all the questions are completed or not
		if (qcount < totalquestions) {
			try {
				resString = getQuestion(session);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			resString="You are done with your interview,Goodbye";
		}
		String responseText = resString;

		return newAskResponse(responseText, responseText);
	}
	private SpeechletResponse yesMethod(Intent intent,final Session session)
	{
		Slot Yes=intent.getSlot("type");
        String user_ans=Yes.getValue();
        String resString="";
        String id=(String)session.getAttribute("NetraId");
        if(user_ans.equalsIgnoreCase("yes"))
               {
        	System.out.println("url1");
        	String url_is=ec2_ip+"/studentInterview.php?student_id='"+id+"'";
        	String interview_no=urlCode(url_is); 
        	System.out.println("interview no"+interview_no);
        	if(interview_no.equals("no interview scheduled for this roll number")||interview_no.equals("Exception called"))
        	    resString="you are not enrolled for this interview";
        	else
        	{
        		resString="you are enrolled for interview "+interview_no;
        		session.setAttribute("Interview_no", interview_no);
        		System.out.println("url2");
        	String url2_is=ec2_ip+"/interviewNoToQuestion.php?interview='"+interview_no+"'";	
        	String qbank=urlCode(url2_is);
        	System.out.println("question back is "+qbank);
        	if(qbank.equals("no interview scheduled for this roll number")||qbank.equals("Exception called"))
        		resString="you are not enrolled for this interview";
        	else
        	{
        		resString="your qbank is"+qbank;
        		
        		System.out.println("url3");
        		String url3_is=ec2_ip+"/BankToQuestions.php?bank_id="+qbank;	
            	String ques_nos=urlCode(url3_is);
            	answer=ques_nos.split(",");
            	System.out.println(qbank);
            	System.out.println(answer);
            	for(int i=0;i<answer.length;i++)
            	{
            		System.out.println(answer[i]);
            	}
            	resString=getQuestion(session);
        	}
        	}
        }
        else
        {
        	resString="tell your netra id";
        }
        String responseText=resString;
        return newAskResponse(responseText, responseText);
	}
	private SpeechletResponse answerMethod(Intent intent,final Session session) {
		System.out.println("sys1");
		Slot answer_is=intent.getSlot("answer");
		int i=Integer.parseInt(session.getAttribute("qcount").toString());
	        String answer=answer_is.getValue();	
	        String answer_arr[]=answer.split(" ");
	        String user_response="";
	        for(int j=0;j<answer_arr.length-1;j++)
	        {
	        	user_response+=answer_arr[j]+"+";
	        }
	        user_response+=answer_arr[answer_arr.length-1];
	        String correct_ans=session.getAttribute("answer").toString();
	        String correct_arr[]=correct_ans.split(" ");
	        String correct_response="";
	        for(int j=0;j<correct_arr.length-1;j++)
	        {
	        	correct_response+=correct_arr[j]+"+";
	        }
	        correct_response+=correct_arr[correct_arr.length-1];
	        session.setAttribute("ans"+i,answer);
	        String resString = null;String temp;
			try {
				System.out.println("sys2");
				String url_ml="http://ec2-3-121-215-245.eu-central-1.compute.amazonaws.com:5000/compare/"+correct_response+"/"+user_response;
				String grade=urlCode(url_ml);
				String id=session.getAttribute("NetraId").toString();
				int interview_id=Integer.parseInt(session.getAttribute("Interview_no").toString());
				String question=session.getAttribute("question").toString();
				String question_para=formatting(question);
				String correct_ans_para=formatting(correct_ans);
				String answer_para=formatting(answer);
				System.out.println("sys3");
				String update_history_url=ec2_ip+"/putInterviewHistory.php?interview_id="+interview_id+"&question_id="+i+"&student_id='"+id+"'&question='"+question_para+"'&correct_answer='"+correct_ans_para+"'&response_answer='"+answer_para+"'&grade='"+grade+"'";
                String update_info=urlCode(update_history_url);
                System.out.println(update_info);
				session.setAttribute("grade"+i,grade);
temp = getQuestion(session);

resString = temp;
}
catch (Exception e) {
		System.out.println(e.getMessage());
	}

	String responseText = resString;
	return newAskResponse(responseText, responseText);
	}
	 
	private SpeechletResponse repeat(Intent intent,final Session session)
	{

 System.out.println("in repeat method");
 counter=1;
		String resString = null;String temp;
					try {
 temp = getQuestion(session);
 counter=0;
	resString = temp;
}
		catch (Exception e) {
				System.out.println(e.getMessage());
			}

			String responseText = resString;
			return newAskResponse(responseText, responseText);
	}
	public SpeechletResponse startInterview(Intent intent,final Session session)
	{
	     Slot netraid=intent.getSlot("netraid");
	     String netra=netraid.getValue();
	     session.setAttribute("NetraId",netra);
	     session.setAttribute("qcount",0);
	     String resString;
	     resString="Your netra id is "+netra+" to confirm say yes otherwise say no";
	 	String responseText = resString;

		return newAskResponse(responseText, responseText);
		
	}
	private String getQuestion(final Session session) {
  System.out.println("in get question");
		String responseText = "";
		int ques=(int)session.getAttribute("qcount");
		session.setAttribute("totalquestions",5);
		if(ques<4) {
			if(counter==1)
			{
				 
				String question=(String)session.getAttribute("question");
				responseText=question;
			}else {
				  
				ques+=1;
				session.setAttribute("qcount",ques);
				System.out.println("question"+ques);
				System.out.println(answer[ques-1]);
				String url_is=ec2_ip+"/idToQuestion.php?id='"+answer[ques-1]+"'";
	        	String ques_ans=urlCode(url_is); 
	        	System.out.println(ques_ans);
				String ques_arr[]=ques_ans.split("xx===xx");
				String question=ques_arr[0];
				
	        	System.out.println(question);				
				String answer_is=ques_arr[1];
				System.out.println(answer_is);
				session.setAttribute("question",question);
				session.setAttribute("answer", answer_is);
			     responseText=question;
			 }
	}
		else
		{
			for(int i=1;i<=4;i++)
			{
				System.out.println("answer"+i+"="+session.getAttribute("ans"+i)+"  "+"grade"+i+" "+session.getAttribute("grade"+i));
			}
			responseText="thank you for taking the interview,GoodBye";
		}
		return responseText;
		}  
	public String urlCode(String url_id)
	{
		try {
		 URL oracle = new URL(url_id);
	        URLConnection yc = oracle.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
	        String inputLine, result="";
	        while ((inputLine = in.readLine()) != null) 
	            result += inputLine;
	        in.close();	
	        return result;
		} catch(Exception e) {
			e.printStackTrace();
			return "Exception called";
		}
	}
	public static String formatting(String str)
	{
		String ans[]=str.split(" ");
		String result="";
		for(int i=0;i<ans.length-1;i++)
		{
		result+=ans[i]+"%20";
		}
		result+=ans[ans.length-1];
		return result;
	}

	@Override
	public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
		log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
				requestEnvelope.getSession().getSessionId());
		
		// any cleanup logic goes here
	}
	
	private SpeechletResponse newAskResponse(String stringOutput, String repromptText) {
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(stringOutput);

		PlainTextOutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech();
		repromptOutputSpeech.setText(repromptText);
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(repromptOutputSpeech);

		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
	}

}

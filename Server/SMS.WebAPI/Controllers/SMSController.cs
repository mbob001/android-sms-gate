using Microsoft.AspNetCore.Mvc;
using SMS.WebAPI.Models;
using SMS.WebAPI.Repository;

namespace SMS.WebAPI.Controllers
{
    /// <summary>
    /// Sending and receiving SMS messages
    /// </summary>
    [ApiController]
    [Route("[controller]")]
    public class SMSController : ControllerBase
    {
        private readonly ISMSStorage smsStorage;

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="smsStorage"></param>
        public SMSController(ISMSStorage smsStorage)
        {
            this.smsStorage = smsStorage;
        }

        #region Methods for Android application

        /// <summary>
        /// Get SMS to send for Android application
        /// </summary>
        /// <returns></returns>
        [HttpGet("GetSmsToSend")]
        [ProducesResponseType(typeof(IEnumerable<SmsMessage>), StatusCodes.Status200OK)]
        public IActionResult GetSmsToSend()
        {            
            var result = smsStorage.GetSmsToSend();

            return Ok(result);
        }

        /// <summary>
        /// Save received SMS from Android application
        /// </summary>
        /// <param name="sms"></param>
        /// <returns></returns>
        [HttpPost("SaveReceivedSms")]
        [ProducesResponseType(typeof(bool), StatusCodes.Status200OK)]
        public IActionResult SaveReceivedSms(SmsMessage sms)
        {
            smsStorage.SaveReceivedSms(sms);

            return Ok(true);
        }

        #endregion

        #region Methods for web application

        /// <summary>
        /// Get received SMS from Android application
        /// </summary>
        /// <returns></returns>
        [HttpGet("GetReceivedSms")]
        [ProducesResponseType(typeof(IEnumerable<SmsMessage>), StatusCodes.Status200OK)]
        public IActionResult GetReceivedSms()
        {
            var result = smsStorage.GetReceivedSms();

            return Ok(result);
        }

        /// <summary>
        /// Send SMS via Android application
        /// </summary>
        /// <param name="sms"></param>
        /// <returns></returns>
        [HttpPost("SendSms")]
        [ProducesResponseType(typeof(bool), StatusCodes.Status200OK)]
        public IActionResult SendSms(SmsMessage sms)
        {
            smsStorage.SaveSmsToSend(sms);

            return Ok(true);
        }

        #endregion
    }
}

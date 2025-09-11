using SMS.WebAPI.Models;

namespace SMS.WebAPI.Repository
{
    /// <summary>
    /// Interface for storage SMS
    /// </summary>
    public interface ISMSStorage
    {
        /// <summary>
        /// Get SMS to send
        /// </summary>
        /// <returns></returns>
        List<SmsMessage> GetSmsToSend();
        /// <summary>
        /// Save received SMS
        /// </summary>
        /// <param name="sms"></param>
        void SaveReceivedSms(SmsMessage sms);

        /// <summary>
        /// Get received SMS
        /// </summary>
        /// <returns></returns>
        List<SmsMessage> GetReceivedSms();

        /// <summary>
        /// Save SMS to send
        /// </summary>
        /// <param name="sms"></param>
        void SaveSmsToSend(SmsMessage sms);
    }
}

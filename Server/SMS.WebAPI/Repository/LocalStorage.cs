using SMS.WebAPI.Models;

namespace SMS.WebAPI.Repository
{
    /// <summary>
    /// Local memory storage for SMS messages
    /// </summary>
    public class LocalStorage : ISMSStorage
    {
        private static readonly List<SmsMessage> receivedSms = new();
        private static readonly List<SmsMessage> smsToSend = new();

        private object lockReceivedSms = new();
        private object lockSmsToSend = new();

        /// <summary>
        /// Get received SMS
        /// </summary>
        /// <returns></returns>
        public List<SmsMessage> GetReceivedSms()
        {
            lock (lockReceivedSms)
            {
                return receivedSms.ToList();
            }
        }

        /// <summary>
        /// Get SMS to send
        /// </summary>
        /// <returns></returns>
        public List<SmsMessage> GetSmsToSend()
        {
            lock (lockSmsToSend)
            {
                var result = smsToSend.ToList();
                smsToSend.Clear();
                return result;
            }
        }

        /// <summary>
        /// Save received SMS
        /// </summary>
        /// <param name="sms"></param>
        public void SaveReceivedSms(SmsMessage sms)
        {
            lock (lockReceivedSms)
            {
                receivedSms.Add(sms);
            }
        }

        /// <summary>
        /// Save SMS to send
        /// </summary>
        /// <param name="sms"></param>
        public void SaveSmsToSend(SmsMessage sms)
        {
            lock (lockSmsToSend)
            {
                smsToSend.Add(sms);
            }
        }
    }
}

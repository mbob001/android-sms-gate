namespace SMS.WebAPI.Models
{
    /// <summary>
    /// SMS message
    /// </summary>
    public class SmsMessage
    {
        /// <summary>
        /// Phone number
        /// </summary>
        public required string Phone { get; set; }
        /// <summary>
        /// Message
        /// </summary>
        public required string Message { get; set; }
    }
}

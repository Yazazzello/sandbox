package by.yazazzello.forum.client.helpers

import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import retrofit2.HttpException
import kotlin.test.assertEquals

class LottieErrorMapperTest {

    @Test
    fun `should correct map to lotties`() {
        assertEquals("error_message.json",LottieErrorMapper.getLottieByError(mock<HttpException>()))
        assertEquals("empty_box.json",LottieErrorMapper.getLottieByError(mock<NullPointerException>()))
    }
}
package com.example.android.fact_check;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    private Gson gson = new Gson();
    private JSONObject jsonObject;
    private ArrayList<String> imageUrl;
    private ArrayList<String> website_url;
    @Mock
    public Search search;

    @Spy
    private ModelClass mockedModelClass;

    @Before
    public void setup() {
        mockedModelClass = new ModelClass();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void ModelClass_isCorrect() {
        String response = "{\"claims\":[{\"text\":\"Video shows “Khalistan Zindabad” slogans being raised during Modi’s visit to Punjab on January 5. The PM’s convoy was stopped due to protests some distance away from his scheduled public meeting in Ferozepur.\",\"claimant\":\"Social media users\",\"claimDate\":\"2022-01-06T00:00:00Z\",\"claimReview\":[{\"publisher\":{\"name\":\"India Today\",\"site\":\"indiatoday.in\"},\"url\":\"https:\\/\\/www.indiatoday.in\\/fact-check\\/story\\/fact-check-this-pro-khalistan-rally-is-not-linked-to-pm-modi-s-punjab-visit-1897011-2022-01-07\",\"title\":\"Fact Check: This pro-Khalistan rally is not linked to PM Modi's ...\",\"reviewDate\":\"2022-01-07T00:00:00Z\",\"textualRating\":\"Half true\",\"languageCode\":\"en\"}]},{\"text\":\"Prime Minister Narendra Modi was booed by the people of Varanasi during his recent trip which the media never showed. People shouted anti-Modi slogans while he was walking down the streets with Yogi Adityanath.\",\"claimant\":\"Social media users\",\"claimDate\":\"2021-12-22T00:00:00Z\",\"claimReview\":[{\"publisher\":{\"name\":\"India Today\",\"site\":\"indiatoday.in\"},\"url\":\"https:\\/\\/www.indiatoday.in\\/fact-check\\/story\\/fact-check-viral-video-of-anti-modi-protests-in-varanasi-is-doctored-1891006-2021-12-22\",\"title\":\"Fact Check: Viral video of anti-Modi protests in Varanasi is doctored\",\"reviewDate\":\"2021-12-22T00:00:00Z\",\"textualRating\":\"False\",\"languageCode\":\"en\"}]},{\"text\":\"Mob chants anti Modi slogans in Varanasi\",\"claimant\":\"Facebook user Nitin Kumar\",\"claimDate\":\"2016-06-20T00:00:00Z\",\"claimReview\":[{\"publisher\":{\"name\":\"Alt News\",\"site\":\"altnews.in\"},\"url\":\"https:\\/\\/www.altnews.in\\/viral-video-of-locals-chanting-anti-modi-slogans-in-up-is-doctored\\/\",\"title\":\"Viral video of locals chanting anti-Modi slogans in UP is doctored\",\"reviewDate\":\"2021-12-25T00:00:00Z\",\"textualRating\":\"False\",\"languageCode\":\"en\"}]},{\"text\":\"Congress spokesperson has lauded Narendra Modi as a “hero” in a TV debate and accepted that it’s “very difficult” for Rahul Gandhi to compete with him.\",\"claimant\":\"Social media users\",\"claimDate\":\"2021-12-20T00:00:00Z\",\"claimReview\":[{\"publisher\":{\"name\":\"India Today\",\"site\":\"indiatoday.in\"},\"url\":\"https:\\/\\/www.indiatoday.in\\/fact-check\\/story\\/fact-check-tv-debate-clip-congress-spokesperson-hailing-pm-narendra-modi-1889702-2021-12-20\",\"title\":\"Fact Check: TV debate clip falsely shared as Congress ...\",\"reviewDate\":\"2021-12-20T00:00:00Z\",\"textualRating\":\"Mostly false\",\"languageCode\":\"en\"}]},{\"text\":\"Prime Minister Narendra Modi humiliated in Italy, had to take a taxi for his trips.\",\"claimant\":\"Social media users\",\"claimDate\":\"2021-10-31T00:00:00Z\",\"claimReview\":[{\"publisher\":{\"name\":\"India Today\",\"site\":\"indiatoday.in\"},\"url\":\"https:\\/\\/www.indiatoday.in\\/fact-check\\/story\\/fact-check-netizens-drive-on-the-wrong-side-of-facts-on-pm-modi-s-italy-tour-1871797-2021-10-31\",\"title\":\"Fact Check: Netizens drive on the wrong side of facts on PM Modi's ...\",\"reviewDate\":\"2021-10-31T00:00:00Z\",\"textualRating\":\"Mostly false\",\"languageCode\":\"en\"}]}],\"nextPageToken\":\"CAU\"}";

        search = gson.fromJson(response, Search.class);

        doCallRealMethod().when(mockedModelClass).setClaim(search.claims.get(0).text);
        doCallRealMethod().when(mockedModelClass).setClaimant(search.claims.get(0).claimant);
        doCallRealMethod().when(mockedModelClass).setReview(search.claims.get(0).claimReview.get(0).textualRating);
        when(mockedModelClass.getClaim()).thenReturn(search.claims.get(0).text);
        when(mockedModelClass.getClaimant()).thenReturn(search.claims.get(0).claimant);
        when(mockedModelClass.getReview()).thenReturn(search.claims.get(0).claimReview.get(0).textualRating);
        System.out.println(mockedModelClass.getClaim());
        verify(mockedModelClass, times(1)).getClaim();
        verify(mockedModelClass).getClaimant();
        verify(mockedModelClass).getReview();
    }
}
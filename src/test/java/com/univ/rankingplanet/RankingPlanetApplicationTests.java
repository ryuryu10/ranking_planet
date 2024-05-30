package com.univ.rankingplanet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class RankingPlanetApplicationTests {

	@Test
	void contextLoads() {
		System.out.println(solution(">", "!", 41, 78));
	}

	public int solution(String ineq, String eq, int n, int m) {
		if (ineq == ">"){
			if(eq == "="){
				return n >= m ? 1 : 0;
			}else{
				return n > m ? 1 : 0;
			}
		}else{
			if(eq == "="){
				return n <= m ? 1 : 0;
			}else{
				return n < m ? 1 : 0;
			}

		}
	}

}

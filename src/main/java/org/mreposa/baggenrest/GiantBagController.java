package org.mreposa.baggenrest;

import java.util.ArrayList;
import java.util.Set;

import org.mreposa.baggenrest.bagitem.GiantBagItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GiantBagController {

	@GetMapping("/generate-bag")
	public GiantBagList generateBag(@RequestParam(value = "count", defaultValue = "1") String count) {
		GiantBagGenerator gen = new GiantBagGenerator();
		int bagCount = Integer.parseInt(count);
		ArrayList<GiantBag> bags = new ArrayList<>();

		for (int i = 0; i < bagCount; i++) {
			Set<GiantBagItem> bag = gen.generate();

			StringBuilder output = new StringBuilder();
			int c = 0;
			for (GiantBagItem giantBagItem : bag) {
				output.append(giantBagItem.toString());
				c++;
				if (c < bag.size()) {
					output.append(",");
				}
			}
			GiantBag giantBag = new GiantBag(i + 1, output.toString());
			bags.add(giantBag);
		}

		return new GiantBagList(bags);
	}
}

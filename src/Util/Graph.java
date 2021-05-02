package Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class Graph {
	private int[] cs = {10, 11, 12, 13, 14, 15, 16, 17, 5, 6};
	private int[] color = {10, 11, 12, 13, 14, 15, 16, 17, 5, 6, 7, 28, 29, 8, 9, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27};

	private int n;
	private int[] val;
	private int[] deg, colorChoice;
	private int colorChoiceNum;
	private boolean[] spilled, colored;
	private int maxColor = 0, normColor = 13;
	private ArrayList<ArrayList<Integer>> edges = new ArrayList<>();

	public void addEdge(int x, int y) {
		if (x != y) {
			edges.get(x).add(y);
			edges.get(y).add(x);
		}
	}

	public boolean[] saved;

    public Graph(int _n) {
		n = _n + cs.length;
		saved = new boolean[n];
		for (int i = 0; i < n; ++i)
			edges.add(new ArrayList<>());
    }


	private void init() {
		for (int i = 0; i < n; ++i) {
			HashSet<Integer> tempHashSet = new LinkedHashSet<>(edges.get(i));
			edges.set(i, new ArrayList<>(tempHashSet));
		}
		deg = new int[n]; val = new int[n];
		for (int i = 0; i < n; ++i) val[i] = -1;
		spilled = new boolean[n];
		colored = new boolean[n];
	}

	public void work() {
		init();
		ArrayList<Integer> spillArr = new ArrayList<>();
		ArrayList<Integer> savedArr = new ArrayList<>();
		for (int i = 0; i < n; ++i) {
			if (saved[i]) {
				savedArr.add(i);
				edges.get(i).forEach(x -> deg[x]++);
			}else{
				edges.get(i).forEach(x -> {
					if (!saved[x]) deg[x]++;
				});
			}
		}
		for (int i = n - cs.length; i < n; ++i){
			val[i] = i - (n - cs.length); colored[i] = true;
		}

		colorChoice = new int [n];
		colorChoiceNum = 0;

		for (int i : savedArr) {
			if (deg[i] < color.length - normColor) {
				colorChoice[colorChoiceNum++] = i; colored[i] = true;
			} else { spillArr.add(i); }
		}

 		for (int i = 0, head = 0; ; ++i){
			while (i == colorChoiceNum && head < spillArr.size()){
				int x = spillArr.get(head++);
				if (!colored[x] && !spilled[x]){
					spilled[x] = true;
					edges.get(x).forEach(y -> {
						if (!colored[y] && !spilled[y]){
							if (--deg[y] < color.length - normColor && saved[y]){
								colorChoice[colorChoiceNum++] = y;
								colored[y] = true;
							}
						}
					});
				}
			}
			if (i >= colorChoiceNum) break;
			int v = colorChoice[i];
			edges.get(v).forEach(x -> {
				if (saved[x] && !colored[x] && !spilled[x]){
					if (--deg[x] < color.length - normColor){
						colorChoice[colorChoiceNum++] = x;
						colored[x] = true;
					}
				}
			});
		}

		for (int i = colorChoiceNum - 1; i >= 0; --i) {
			int v = colorChoice[i];
			boolean[] used = new boolean[color.length];
			for (int j = 0; j < edges.get(v).size(); j++) {
				int x = edges.get(v).get(j);
				if (!spilled[x] && val[x] != -1) used[val[x]] = true;
			}
			int now = normColor;
			while (used[now]) now++;
			val[v] = now;
			if (now > maxColor) maxColor = now;
		}

		colorChoiceNum = 0;
		spillArr.clear();
		for (int i = 0; i < n; ++i){
			if (!colored[i] && !spilled[i] && deg[i] < color.length) {
				colorChoice[colorChoiceNum++] = i;
				colored[i] = true;
			}
			spillArr.add(i);
		}
		spillArr.sort((Integer a, Integer b) -> Integer.compare(deg[b], deg[a]));
		int rotate_num = 20;
		if (spillArr.size() > rotate_num){
			ArrayList<Integer> new_spill_arr = new ArrayList<>();
			for (int i = rotate_num; i < spillArr.size(); ++i) new_spill_arr.add(spillArr.get(i));
			for (int i = 0; i < rotate_num; ++i) new_spill_arr.add(spillArr.get(i));
			spillArr = new_spill_arr;
		}
		
 		for (int i = 0, head = 0; ; ++i){
			while (i == colorChoiceNum && head < spillArr.size()){
				int x = spillArr.get(head++);
				if (!colored[x] && !spilled[x]){
					spilled[x] = true;
					edges.get(x).forEach(y -> {
						if (!colored[y] && !spilled[y]){
							if (--deg[y] < color.length){
								colorChoice[colorChoiceNum++] = y;
								colored[y] = true;
							}
						}
					});
				}
			}
			if (i >= colorChoiceNum) break;
			int v = colorChoice[i];
			edges.get(v).forEach(x -> {
				if (!colored[x] && !spilled[x]){
					if (--deg[x] < color.length){
						colorChoice[colorChoiceNum++] = x;
						colored[x] = true;
					}
				}
			});
		}
		for (int i = colorChoiceNum - 1; i >= 0; --i){
			int v = colorChoice[i];
			boolean[] used = new boolean[color.length];
			for (int j = 0; j < edges.get(v).size(); j++){
				int x = edges.get(v).get(j);
				if (!spilled[x] && val[x] != -1) used[val[x]] = true;
			}
			int now = 0;
			while (used[now]) now++;
			val[v] = now;
			if (now > maxColor) maxColor = now;
		}
	}

	public int getColor(int x){ return (val[x] >= 0) ? color[val[x]] : -1; }
	public int useSaved(){ return (maxColor >= normColor) ? maxColor - normColor + 1 : 0; }
	public int[] getCs() { return cs; }
}

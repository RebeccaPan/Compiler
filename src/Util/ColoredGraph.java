package Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class ColoredGraph {
	public static int normColor = 13;
	public static int[] colorSet = { 10, 11, 12, 13, 14, 15, 16, 17, 5, 6 };
	public static int colorSetLen = colorSet.length;
	private int maxColor = 0;
	private int[] color = {10, 11, 12, 13, 14, 15, 16, 17, 5, 6, 7, 28, 29, 8, 9, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27};
	private int[] deg, colorChoice;
	private int colorChoiceNum;
	private boolean[] spilled, colored;
	private int[] val;

	private int n;
	private boolean[] saved;
	private ArrayList<ArrayList<Integer>> edges;

	public void addEdge(int x, int y) {
    	if (x == y) return;
    	edges.get(x).add(y); edges.get(y).add(x);
	}

    public ColoredGraph(int _n) {
    	n = _n + colorSetLen;
    	edges = new ArrayList<>();
    	for (int i = 0; i < n; ++i)
    		edges.add(new ArrayList<>());
		saved = new boolean[n];
    }

	private void init() {
		for (int i = 0; i < n; ++i) {
			HashSet<Integer> tempHashSet = new LinkedHashSet<>(edges.get(i));
			edges.set(i, new ArrayList<>(tempHashSet));
		}
		val = new int[n];
		for (int i = 0; i < n; ++i) val[i] = -1;
		spilled = new boolean[n];
		colored = new boolean[n];
		deg = new int[n];
	}

	public void work() {
		init();
		ArrayList<Integer> spillArr = new ArrayList<>();
		ArrayList<Integer> savedArr = new ArrayList<>();
		for (int i = 0; i < n; ++i) {
			if (saved[i]) {
				savedArr.add(i);
				for (Integer e : edges.get(i)) deg[e]++;
			} else {
				for (Integer e : edges.get(i))
					if (!saved[e]) deg[e]++;
			}
		}
		for (int i = n - colorSetLen; i < n; ++i) {
			colored[i] = true;
			val[i] = i - (n - colorSetLen);
		}

		colorChoice = new int[n];
		colorChoiceNum = 0;
		for (int i : savedArr) {
			if (deg[i] < colorSetLen - normColor) {
				colored[i] = true;
				colorChoice[colorChoiceNum++] = i;
			} else {
				spillArr.add(i);
			}
		}
		int top = 0;
 		for (int i = 0; ; ++i) {
			while (i == colorChoiceNum && top < spillArr.size()) {
				int x = spillArr.get(top++);
				if (!colored[x] && !spilled[x]){
					spilled[x] = true;
					for (Integer e : edges.get(x)) {
						if (!colored[e] && !spilled[e]){
							if (--deg[e] < colorSetLen - normColor && saved[e]){
								colored[e] = true;
								colorChoice[colorChoiceNum++] = e;
							}
						}
					}
				}
			}
			if (i >= colorChoiceNum) break;
			for (Integer e : edges.get(colorChoice[i])) {
				if (saved[e] && !colored[e] && !spilled[e]) {
					if (--deg[e] < colorSetLen - normColor) {
						colorChoice[colorChoiceNum++] = e;
						colored[e] = true;
					}
				}
			}
		}
		for (int i = colorChoiceNum - 1; i >= 0; --i) {
			int v = colorChoice[i];
			boolean[] used = new boolean[colorSetLen];
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
			if (!colored[i] && !spilled[i] && deg[i] < colorSetLen) {
				colorChoice[colorChoiceNum++] = i;
				colored[i] = true;
			}
			spillArr.add(i);
		}
		spillArr.sort((Integer a, Integer b) -> Integer.compare(deg[b], deg[a]));
		int rotate_num = 20;
		if (spillArr.size() > rotate_num) {
			ArrayList<Integer> new_spill_arr = new ArrayList<>();
			for (int i = rotate_num; i < spillArr.size(); ++i) new_spill_arr.add(spillArr.get(i));
			for (int i = 0; i < rotate_num; ++i) new_spill_arr.add(spillArr.get(i));
			spillArr = new_spill_arr;
		}
 		for (int i = 0, head = 0; ; ++i) {
			while (i == colorChoiceNum && head < spillArr.size()) {
				int x = spillArr.get(head++);
				if (!colored[x] && !spilled[x]){
					spilled[x] = true;
					for (Integer edge : edges.get(x)) {
						if (!colored[edge] && !spilled[edge]){
							if (--deg[edge] < colorSetLen){
								colorChoice[colorChoiceNum++] = edge;
								colored[edge] = true;
							}
						}
					}
				}
			}
			if (i >= colorChoiceNum) break;
			int v = colorChoice[i];
			edges.get(v).forEach(x -> {
				if (!colored[x] && !spilled[x]){
					if (--deg[x] < colorSetLen){
						colorChoice[colorChoiceNum++] = x;
						colored[x] = true;
					}
				}
			});
		}
		for (int i = colorChoiceNum - 1; i >= 0; --i){
			int v = colorChoice[i];
			boolean[] used = new boolean[colorSetLen];
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

	public boolean[] getSaved() { return saved; }
	public int useSaved(){ return (maxColor >= normColor) ? maxColor - normColor + 1 : 0; }
	public int getColor(int x) { return (val[x] >= 0) ? color[val[x]] : -1; }
}

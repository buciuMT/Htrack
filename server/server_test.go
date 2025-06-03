package main

import "testing"

func Two_plus_two_test(t *testing.T) {
	if 2+2 != 4 {
		t.Errorf("two_plus_two_test failed\n")
	}
}

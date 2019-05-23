package com.capstone.wowu;

import com.capstone.wowu.Classifier.Recognition;

import java.util.List;

import ai.fritz.vision.FritzVisionLabel;

public interface ResultsView {
    void setResults(final List<Recognition> results);

    void setResult(final List<FritzVisionLabel> labels);
}

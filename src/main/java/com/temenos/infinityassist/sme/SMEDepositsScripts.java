package com.temenos.infinityassist.sme;

/**
 * This class was automatically generated by the data modeler tool.
 */

public class SMEDepositsScripts implements java.io.Serializable {

	static final long serialVersionUID = 1L;

	public SMEDepositsScripts() {
	}

	public static void setRequestIdAndDocumentList(
			org.kie.api.runtime.process.ProcessContext kcontext) {
		try {
			String response = kcontext.getVariable("response").toString();
			org.json.JSONObject responseJSON = new org.json.JSONObject(response);
			kcontext.setVariable("requestId",
					responseJSON.getString("requestId"));
			kcontext.setVariable("documentList",
					responseJSON.optJSONArray("documentsList").toString());
		} catch (Exception e) {

		}
	}

	public static void setCompanyIds(
			org.kie.api.runtime.process.ProcessContext kcontext) {
		try {
			String response = (String) kcontext.getVariable("response");
			org.json.JSONObject responseJSON = new org.json.JSONObject(response);
			kcontext.setVariable("companyCifId",
					responseJSON.getString("coreCustomerId"));
			kcontext.setVariable("companyPartyId",
					responseJSON.getString("partyID"));
		} catch (Exception e) {

		}
	}

	public static void setParties(
			org.kie.api.runtime.process.ProcessContext kcontext,
			String relatedPartiesResponse) {
		try {
			org.json.JSONObject relatedParties = new org.json.JSONObject(
					relatedPartiesResponse);
			org.json.JSONArray relatedPartiesArray = relatedParties
					.getJSONArray("relatedParties");
			java.util.Set<String> prospectParties = new java.util.HashSet<>();
			java.util.Set<String> existingParties = new java.util.HashSet<>();
			java.util.Map<String, String> partyMap = new java.util.HashMap<>();
			for (int index = 0; index < relatedPartiesArray.length(); index++) {
				org.json.JSONObject party = relatedPartiesArray
						.getJSONObject(index);
				String partyId = party.getString("relatedPartyId");
				String role = party.getString("relatedPartyRole");
				partyMap.put(partyId, role);
				if (partyId.startsWith("NNVF")) {
					if (role.equals("01")) {
						kcontext.setVariable("isCompanyExisting", false);
					} else {
						prospectParties.add(partyId);
					}
				} else if (partyId.startsWith("ENVF")) {
					if (role.equals("01")) {
						kcontext.setVariable("existingNonverifiedCompanyId",
								partyId);
						kcontext.setVariable("isCompanyExisting", true);
					} else {
						existingParties.add(partyId);
					}
				}
			}
			kcontext.setVariable("partyRoleMap", partyMap);
			kcontext.setVariable("prospectParties", prospectParties);
			kcontext.setVariable("existingParties", existingParties);
		} catch (Exception e) {

		}
	}

	public static void getFacilities(
			org.kie.api.runtime.process.ProcessContext kcontext,
			String facilitiesResponse) {
		try {
			org.json.JSONObject facilities = new org.json.JSONObject(
					facilitiesResponse);
			org.json.JSONArray facilitiesArray = facilities
					.getJSONArray("facilities");
			java.util.List<String> facilityList = new java.util.ArrayList<String>();
			kcontext.setVariable("reviewRequired", false);
			String facilityId = "";
			for (int index = 0; index < facilitiesArray.length(); index++) {
				org.json.JSONObject facility = facilitiesArray
						.getJSONObject(index);
				if (facility.get("approvalStatusId").toString().equals("03")) {
					facilityList.add(facility.get("facilityId").toString());
					kcontext.setVariable("reviewRequired", true);
				}
				facilityId = facility.get("facilityId").toString();
			}
			kcontext.setVariable("facilityId", facilityId);
			kcontext.setVariable("facilitiesList", facilityList);
		} catch (Exception e) {

		}
	}

	public static void setTransactionId(
			org.kie.api.runtime.process.ProcessContext kcontext) {
		try {
			String response = (String) kcontext.getVariable("response");
			org.json.JSONObject responseJSON = new org.json.JSONObject(response);
			kcontext.setVariable("transactionId",
					responseJSON.getString("transactionId"));
		} catch (Exception e) {

		}
	}

	public static void checkDepositsDecision(
			org.kie.api.runtime.process.ProcessContext kcontext,
			String decisionResponse) {
		try {
			org.json.JSONObject depositsDecisions = new org.json.JSONObject(
					decisionResponse);
			if (depositsDecisions.has("decisions")) {
				org.json.JSONArray decisionsArray = depositsDecisions
						.getJSONArray("decisions");
				String facilityId = kcontext.getVariable("facilityId")
						.toString();
				for (int index = 0; index < decisionsArray.length(); index++) {
					org.json.JSONObject decision = decisionsArray
							.getJSONObject(index);
					if (decision.get("facilityId").toString()
							.equals(facilityId)
							&& decision.get("finalDecision").toString()
									.equals("true")
							&& (decision.get("decisionId").toString()
									.equals("02") || decision.get("decisionId")
									.toString().equals("03"))) {
						kcontext.setVariable("hasDecision", true);
						if (decision.get("decisionId").toString().equals("02")) {
							kcontext.setVariable("approvalStatusId", "01");
						} else if (decision.get("decisionId").toString()
								.equals("03")) {
							kcontext.setVariable("approvalStatusId", "02");
						}
						break;
					} else {
						kcontext.setVariable("hasDecision", false);
					}
				}
			} else {
				kcontext.setVariable("hasDecision", false);
			}
		} catch (Exception e) {

		}
	}

}

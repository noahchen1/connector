package com.example.connector.dto;

import java.util.List;

public class CustomerResponseDto {
    private List<Link> links;
    private int count;
    private boolean hasMore;
    private List<CustomerItem> items;
    private int offset;
    private int totalResults;

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<CustomerItem> getItems() {
        return items;
    }

    public void setItems(List<CustomerItem> items) {
        this.items = items;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public static class Link {
        private String rel;
        private String href;

        public String getRel() {
            return rel;
        }

        public void setRel(String rel) {
            this.rel = rel;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        @Override
        public String toString() {
            return "Link{rel='" + rel + "', href='" + href + "'}";
        }
    }

    public static class CustomerItem {
        private List<Link> links;
        private String cust_id;
        private String email;
        private String firstname;
        private String lastname;

        public List<Link> getLinks() {
            return links;
        }

        public void setLinks(List<Link> links) {
            this.links = links;
        }

        public String getCust_id() {
            return cust_id;
        }

        public void setCust_id(String cust_id) {
            this.cust_id = cust_id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        @Override
        public String toString() {
            return "CustomerItem{cust_id='" + cust_id + "', email='" + email + "', firstname='" + firstname
                    + "', lastname='" + lastname + "'}";
        }
    }

    @Override
    public String toString() {
        return "CustomerResponseDto{" +
                "links=" + links +
                ", count=" + count +
                ", hasMore=" + hasMore +
                ", items=" + items +
                ", offset=" + offset +
                ", totalResults=" + totalResults +
                '}';
    }
}